package net.oneandone.concierge.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.configuration.Resolvers;
import net.oneandone.concierge.resource.helper.ResourceIdentifier;
import net.oneandone.concierge.resource.response.ApiResourcePaging;
import net.oneandone.concierge.resource.response.ApiResponse;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenericApiResource {

    private final List<ExtensionResolver> extensionResolvers;
    private final List<GroupResolver> groupResolvers;

    public GenericApiResource(final Resolvers resolvers) {
        Preconditions.checkNotNull(resolvers, "the resolvers may not be null");

        this.groupResolvers = resolvers.getGroupResolvers();
        this.extensionResolvers = resolvers.getExtensionResolvers();
    }

    @OPTIONS
    @Path("/{uri:.*}")
    public Response getOptions(@PathParam("uri") String uri) {
        final ResourceIdentifier resourceIdentifier = ResourceIdentifier.parse(uri);
        final String[] resolverPath = resourceIdentifier.getCompleteResolverHierarchy();

        final Optional<GroupResolver> groupResolver = groupResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverPath)).findAny();
        if (!groupResolver.isPresent()) {
            final Optional<ExtensionResolver> extensionResolver = extensionResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverPath)).findAny();
            if (!extensionResolver.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        return Response.ok().header("Accept", "GET, OPTIONS").build();
    }

    @GET
    @Path("/{uri:.*}")
    public Response getResource(@Context HttpServletRequest request,
                                @PathParam("uri") String uri) {
        // get all query parameters
        final Multimap<String, String> parametersMultimap = HashMultimap.create();
        final Map<String, String[]> requestParameters = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParameters.entrySet()) {
            parametersMultimap.putAll(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        // resolve resource identifier
        final ResourceIdentifier resourceIdentifier = ResourceIdentifier.parse(uri, parametersMultimap);

        final ApiResponse apiResponse = getResponse(resourceIdentifier);
        if (apiResponse == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final JsonStructure jsonResponse = apiResponse.getObject();
        if (jsonResponse != null) {
            final Response.ResponseBuilder responseBuilder = Response.ok(jsonResponse.toString());
            if (apiResponse.getPaging().isPresent()) {
                responseBuilder.header("Accept-Ranges", apiResponse.getPaging().get().getAcceptRanges());
                responseBuilder.header("Content-Range", apiResponse.getPaging().get().getContentRange());
            }
            responseBuilder.header("Last-Modified", apiResponse.getLastModified().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            return responseBuilder.build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Creates a response for the specified resourceIdentifier.
     *
     * @param resourceIdentifier the URI
     * @return the response or null
     */
    private ApiResponse getResponse(final ResourceIdentifier resourceIdentifier) {
        // return root groups
        if (resourceIdentifier.isEmpty()) {
            final List<String> rootGroups = groupResolvers.stream().filter(r -> r.hierarchy().length == 1).map(GroupResolver::name).collect(Collectors.toList());
            return ApiResponse.create(getLinks(resourceIdentifier, rootGroups, Collections.emptyList()), ZonedDateTime.now());
        }

        return getResponse(resourceIdentifier, null);
    }

    /**
     * Returns the JSON representation for the specified {@code addresses} wrapped in a {@link ApiResponse} or {@code null} if no resource could be found for the specified address.
     *
     * @param parent     the parent element or {@code null} at the root of the resource graph
     * @return the response or {@code null}
     */
    private ApiResponse getResponse(final ResourceIdentifier resourceIdentifier, final Element parent) {
        final String[] resolverHierarchy = resourceIdentifier.getResolverHierarchy();

        final Optional<GroupResolver> resolver = groupResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverHierarchy)).findAny();

        if (resolver.isPresent()) {
            return getGroupResponse(resourceIdentifier, parent, resolver.get());
        } else {
            final Optional<ExtensionResolver> extensionResolver = extensionResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverHierarchy)).findAny();
            if (parent != null && extensionResolver.isPresent()) {
                final Optional<Extension> extension = extensionResolver.get().resolve(parent);
                if (extension.isPresent()) {
                    return ApiResponse.create(extension.get().content(), extension.get().lastModified());
                }
                return null;
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the response for a {@link Group} or {@link Element}.
     *
     * @param parent     the parent element or {@code null} at the root of the resource graph
     * @param resolver   the resolver for the group with the specified {@code groupName}
     * @return the response
     */
    private ApiResponse getGroupResponse(final ResourceIdentifier resourceIdentifier, Element parent, GroupResolver resolver) {
        final Filters filters = resourceIdentifier.getFilters();
        final Group group = resolver.elements(parent, filters);

        final Multimap<Element, Extension> extensionMultimap = HashMultimap.create();
        for (final String extension : resourceIdentifier.getExtensions()) {
            final String[] extendedResolverHierrchy = resourceIdentifier.getExtendedResolverHierrchy(extension);
            final List<ExtensionResolver> extensionResolvers = this.extensionResolvers.stream().filter(e -> Arrays.equals(e.hierarchy(), extendedResolverHierrchy)).collect(Collectors.toList());
            for (final ExtensionResolver extensionResolver : extensionResolvers) {
                final Map<Element, Extension> resolvedExtensions = extensionResolver.resolve(group);
                for (final Map.Entry<Element, Extension> elementExtensionEntry : resolvedExtensions.entrySet()) {
                    extensionMultimap.put(elementExtensionEntry.getKey(), elementExtensionEntry.getValue());
                }
            }
        }

        if (resourceIdentifier.isElementIdentifier()) {
            if (group != null && !group.elements().isEmpty()) {
                final Element element = group.elements().get(0);
                return ApiResponse.create(getExtendedJsonStructure(resourceIdentifier, element, extensionMultimap.get(element)), element.lastModified());
            } else {
                return null;
            }
        } else {
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (final Element element : group.elements()) {
                arrayBuilder.add(getExtendedJsonStructure(resourceIdentifier, element, extensionMultimap.get(element)));
            }

            if (resolver.defaultPageSize() > 0) {
                final int page;
                final int perPage;
                final Optional<PageFilter> pageFilter = filters.get(PageFilter.class);
                if (pageFilter.isPresent()) {
                    page = pageFilter.get().getPage();
                    perPage = pageFilter.get().getPerPage().orElse(resolver.defaultPageSize());
                } else {
                    page = 1;
                    perPage = resolver.defaultPageSize();
                }

                final ApiResourcePaging paging = ApiResourcePaging.builder().group(resourceIdentifier.getGroupOrExtensionIdentifier()).page(page).perPage(perPage).total(group.total()).build();
                return ApiResponse.create(arrayBuilder.build(), group.lastModified(), paging);
            }
            return ApiResponse.create(arrayBuilder.build(), group.lastModified());
        }
    }

    /**
     * Returns the {@link JsonStructure} for the parent element if {@code restOfUri} is empty or else forwards the request to
     * {@link #getResponse(ResourceIdentifier, Element)} with the current parent element and selects recursively the result of it.
     *
     * @param parent             the parent element
     * @param resolvedExtensions a list of all resolved extensions so far
     * @return the JSON representation for the parent element or the result of a forwarded request
     */
    private JsonStructure getExtendedJsonStructure(final ResourceIdentifier resourceIdentifier, final Element parent, final Collection<Extension> resolvedExtensions) {
        if (!resourceIdentifier.isFinalPart()) {
            if (resourceIdentifier.hasNextPart()) {
                final ApiResponse response = getResponse(resourceIdentifier.getNextPart(), parent);
                if (response != null) {
                    return response.getObject();
                }
            }
            return null;
        } else {
            final ResourceIdentifier extendedResourceIdentifier = resourceIdentifier.extend(parent);
            final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            // clone JSON element
            final JsonObject jsonObject = parent.content();
            for (final String key : jsonObject.keySet()) {
                objectBuilder.add(key, jsonObject.get(key));
            }

            // add resolved extensions to cloned element
            for (final Extension extension : resolvedExtensions) {
                objectBuilder.add(extension.address(), extension.content());
            }

            // check for missing extensions and add them
            if (extendedResourceIdentifier.isExtensible()) {
                for (final String extension : extendedResourceIdentifier.getExtensions()) {
                    final ResourceIdentifier extendedIdentifier = extendedResourceIdentifier.getExtendedIdentifier(extension);

                    final Optional<GroupResolver> resolver = groupResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), extendedIdentifier.getCompleteResolverHierarchy())).findAny();
                    if (resolver.isPresent()) {
                        final ApiResponse apiResponse = getResponse(extendedIdentifier);
                        if (apiResponse != null && apiResponse.getObject() != null) {
                            objectBuilder.add(extension, apiResponse.getObject());
                        }
                    }
                }
            }

            final List<String> availableSubgroups = groupResolvers.stream()
                    .filter(r -> r.hierarchy().length == resourceIdentifier.getResolverHierarchy().length + 1 && Arrays.equals(Arrays.copyOfRange(r.hierarchy(), 0, r.hierarchy().length - 1), resourceIdentifier.getResolverHierarchy()))
                    .map(r -> r.hierarchy()[r.hierarchy().length - 1])
                    .collect(Collectors.toList());
            final List<String> availableExtensions = extensionResolvers.stream()
                    .filter(r -> r.hierarchy().length == resourceIdentifier.getResolverHierarchy().length + 1 && Arrays.equals(Arrays.copyOfRange(r.hierarchy(), 0, r.hierarchy().length - 1), resourceIdentifier.getResolverHierarchy()))
                    .map(r -> r.hierarchy()[r.hierarchy().length - 1])
                    .collect(Collectors.toList());
            if (!availableSubgroups.isEmpty() || !availableExtensions.isEmpty()) {
                objectBuilder.add("links", getLinks(resourceIdentifier.extend(parent), availableSubgroups, availableExtensions));
            }

            return objectBuilder.build();
        }
    }

    private static JsonObject getLinks(final ResourceIdentifier resourceIdentifier, final List<String> availableSubgroups, final List<String> availableExtensions) {
        final JsonObjectBuilder linksBuilder = Json.createObjectBuilder();

        if (!availableSubgroups.isEmpty()) {
            final JsonObjectBuilder groupLinkBuilder = Json.createObjectBuilder();
            for (final String availableSubgroup : availableSubgroups) {
                groupLinkBuilder.add(availableSubgroup, "/" + Stream.concat(Arrays.stream(resourceIdentifier.getCompleteIdentifier()), Stream.of(availableSubgroup)).collect(Collectors.joining("/")));
            }
            linksBuilder.add("groups", groupLinkBuilder.build());
        }

        if (!availableExtensions.isEmpty()) {
            final JsonObjectBuilder extensionLinkBuilder = Json.createObjectBuilder();
            for (final String availableExtension : availableExtensions) {
                extensionLinkBuilder.add(availableExtension, "/" + Stream.concat(Arrays.stream(resourceIdentifier.getCompleteIdentifier()), Stream.of(availableExtension)).collect(Collectors.joining("/")));
            }
            linksBuilder.add("extensions", extensionLinkBuilder.build());
        }

        return linksBuilder.build();
    }

}
