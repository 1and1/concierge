package net.oneandone.concierge.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.api.resolver.Resolver;
import net.oneandone.concierge.resource.response.ApiResourcePaging;
import net.oneandone.concierge.resource.response.ApiResponse;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

import javax.json.*;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SuppressWarnings("WeakerAccess")
public class GenericApiResource {

    private final List<ExtensionResolver> extensionResolvers;
    private final List<GroupResolver> groupResolvers;

    public GenericApiResource(final List<GroupResolver> groupResolvers, final List<ExtensionResolver> extensionResolvers) {
        Preconditions.checkNotNull(groupResolvers, "the group resolvers may not be null");
        Preconditions.checkNotNull(extensionResolvers, "the extension resolvers may not be null");

        this.groupResolvers = Collections.unmodifiableList(groupResolvers);
        this.extensionResolvers = Collections.unmodifiableList(extensionResolvers);
    }

    private static List<String> getAvailableSubResolver(final ResourceIdentifier resourceIdentifier, final List<? extends Resolver> resolvers) {
        return resolvers.stream()
                .filter(r -> r.hierarchy().length == resourceIdentifier.hierarchy().length + 1 && Arrays.equals(Arrays.copyOfRange(r.hierarchy(), 0, r.hierarchy().length - 1), resourceIdentifier.hierarchy()))
                .map(r -> r.hierarchy()[r.hierarchy().length - 1])
                .collect(Collectors.toList());
    }

    private static JsonObject getLinks(final ResourceIdentifier resourceIdentifier, final List<String> availableSubgroups, final List<String> availableExtensions) {
        final JsonObjectBuilder linksBuilder = Json.createObjectBuilder();

        if (!availableSubgroups.isEmpty()) {
            final JsonObjectBuilder groupLinkBuilder = Json.createObjectBuilder();
            for (final String availableSubgroup : availableSubgroups) {
                groupLinkBuilder.add(availableSubgroup, "/" + Stream.concat(Arrays.stream(resourceIdentifier.get()), Stream.of(availableSubgroup)).collect(Collectors.joining("/")));
            }
            linksBuilder.add("groups", groupLinkBuilder.build());
        }

        if (!availableExtensions.isEmpty()) {
            final JsonObjectBuilder extensionLinkBuilder = Json.createObjectBuilder();
            for (final String availableExtension : availableExtensions) {
                extensionLinkBuilder.add(availableExtension, "/" + Stream.concat(Arrays.stream(resourceIdentifier.get()), Stream.of(availableExtension)).collect(Collectors.joining("/")));
            }
            linksBuilder.add("extensions", extensionLinkBuilder.build());
        }

        return linksBuilder.build();
    }

    /**
     * Gets the Response for a certain Request.
     *
     * @param req The Request to work on.
     * @param res The Response object to return.
     * @return String of the content of the Response.
     */
    public String getGetResponse(Request req, Response res) {
        log.info("Got a Request on URl " + req.uri());
        final Multimap<String, String> parametersMultimap = HashMultimap.create();
        final Map<String, String> requestParameters = req.params();
        final QueryParamsMap queryParameters = req.queryMap();
        for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
            parametersMultimap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String[]> entry : queryParameters.toMap().entrySet()) {
            parametersMultimap.putAll(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        // resolve resource identifier
        String uri = req.uri();
        if (uri.startsWith("/")) {
            uri = uri.replaceFirst("/", "");
        }
        final ResourceIdentifier resourceIdentifier = ResourceIdentifier.parse(uri, parametersMultimap);
        final ApiResponse apiResponse = getResponseForResourceIdentifier(resourceIdentifier);
        if (apiResponse == null) {
            res.status(HttpServletResponse.SC_NOT_FOUND);
            return res.body();
        }
        final JsonStructure jsonResponse = apiResponse.getObject();
        if (jsonResponse != null) {
            res.status(HttpServletResponse.SC_OK);
            if (apiResponse.getPaging().isPresent()) {
                //noinspection OptionalGetWithoutIsPresent
                res.header("Accept-Ranges", apiResponse.getPaging().get().getAcceptRanges());
                //noinspection OptionalGetWithoutIsPresent
                res.header("Content-Range", apiResponse.getPaging().get().getContentRange());
            }
            res.header("Last-Modified", apiResponse.getLastModified().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            res.body(jsonResponse.toString());
            return res.body();
        } else {
            res.status(HttpServletResponse.SC_NOT_FOUND);
            return res.body();
        }
    }

    /**
     * Options on a certain URl.
     *
     * @param req The Request to work on.
     * @param res The Response object.
     * @return The content of the response.
     */
    public String getOptionsResponse(Request req, Response res) {
        final ResourceIdentifier resourceIdentifier = ResourceIdentifier.parse(req.uri());
        final String[] resolverPath = resourceIdentifier.completeHierarchy();

        final Optional<GroupResolver> groupResolver = groupResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverPath)).findAny();
        if (!groupResolver.isPresent()) {
            final Optional<ExtensionResolver> extensionResolver = extensionResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), resolverPath)).findAny();
            if (!extensionResolver.isPresent()) {
                res.status(HttpServletResponse.SC_NOT_FOUND);
                return res.body();
            }
        }
        res.status(HttpServletResponse.SC_OK);
        res.header("Accept", "GET, OPTIONS");
        return res.body();
    }

    /**
     * Creates a response for the specified resourceIdentifier.
     *
     * @param resourceIdentifier the URI
     * @return the response or null
     */
    private ApiResponse getResponseForResourceIdentifier(final ResourceIdentifier resourceIdentifier) {
        // return root groups
        if (resourceIdentifier.empty()) {
            final List<String> rootGroups = groupResolvers.stream().filter(r -> r.hierarchy().length == 1).map(GroupResolver::name).collect(Collectors.toList());
            return ApiResponse.create(getLinks(resourceIdentifier, rootGroups, Collections.emptyList()), ZonedDateTime.now());
        }

        return getResponseForResourceIdentifier(resourceIdentifier, null);
    }

    /**
     * Returns the JSON representation for the specified {@code addresses} wrapped in a {@link ApiResponse} or {@code null} if no resource could be found for the specified address.
     *
     * @param parent the parent element or {@code null} at the root of the resource graph
     * @return the response or {@code null}
     */
    private ApiResponse getResponseForResourceIdentifier(final ResourceIdentifier resourceIdentifier, final Element parent) {
        final String[] resolverHierarchy = resourceIdentifier.hierarchy();

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
     * @param parent   the parent element or {@code null} at the root of the resource graph
     * @param resolver the resolver for the group with the specified {@code groupName}
     * @return the response
     */
    private ApiResponse getGroupResponse(final ResourceIdentifier resourceIdentifier, Element parent, GroupResolver resolver) {
        final Filters filters = resourceIdentifier.filters();
        final Group group = resolver.elements(parent, filters);

        final Multimap<Element, Extension> extensionMultimap = HashMultimap.create();
        for (final String extension : resourceIdentifier.extensions()) {
            final String[] extendedResolverHierarchy = resourceIdentifier.extendedHierarchy(extension);
            final List<ExtensionResolver> extensionResolvers = this.extensionResolvers.stream().filter(e -> Arrays.equals(e.hierarchy(), extendedResolverHierarchy)).collect(Collectors.toList());
            for (final ExtensionResolver extensionResolver : extensionResolvers) {
                final Map<Element, Extension> resolvedExtensions = extensionResolver.resolve(group);
                for (final Map.Entry<Element, Extension> elementExtensionEntry : resolvedExtensions.entrySet()) {
                    extensionMultimap.put(elementExtensionEntry.getKey(), elementExtensionEntry.getValue());
                }
            }
        }

        if (resourceIdentifier.hasElementIdentifier()) {
            if (group != null && !group.elements().isEmpty()) {
                final Element element = group.elements().get(0);
                return ApiResponse.create(getExtendedJsonStructure(resourceIdentifier, element, extensionMultimap.get(element)), element.lastModified());
            } else {
                return null;
            }
        } else {
            final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (final Element element : group.elements()) {
                arrayBuilder.add(getExtendedJsonStructure(resourceIdentifier, element, extensionMultimap.get(element)));
            }

            objectBuilder.add(resolver.name(), arrayBuilder.build());
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

                final ApiResourcePaging paging = ApiResourcePaging.builder().group(resourceIdentifier.groupIdentifier()).page(page).perPage(perPage).total(group.total()).build();
                final JsonObjectBuilder linksBuilder = Json.createObjectBuilder();

                String linkPattern = "/" + Arrays.stream(resourceIdentifier.get()).collect(Collectors.joining("/")) + "?page=%d&perPage=%d";

                linksBuilder.add("first", String.format(linkPattern, 1, perPage));
                if (page > 1) {
                    linksBuilder.add("previous", String.format(linkPattern, page - 1, perPage));
                }

                int lastPage = (group.total() / perPage) + 1;
                if (page < lastPage) {
                    linksBuilder.add("next", String.format(linkPattern, page + 1, perPage));
                }
                linksBuilder.add("last", String.format(linkPattern, lastPage, perPage));

                objectBuilder.add("links", linksBuilder.build());
                return ApiResponse.create(objectBuilder.build(), group.lastModified(), paging);
            }
            return ApiResponse.create(objectBuilder.build(), group.lastModified());
        }
    }

    /**
     * Returns the {@link JsonStructure} for the parent element if {@code restOfUri} is empty or else forwards the request to
     * {@link #getResponseForResourceIdentifier(ResourceIdentifier, Element)} with the current parent element and selects recursively the result of it.
     *
     * @param parent             the parent element
     * @param resolvedExtensions a list of all resolved extensions so far
     * @return the JSON representation for the parent element or the result of a forwarded request
     */
    private JsonStructure getExtendedJsonStructure(final ResourceIdentifier resourceIdentifier, final Element parent, final Collection<Extension> resolvedExtensions) {
        if (resourceIdentifier.hasNextScope()) {
            final ApiResponse response = getResponseForResourceIdentifier(resourceIdentifier.next(), parent);
            if (response != null) {
                return response.getObject();
            }
            return null;
        } else {
            final ResourceIdentifier fullResourceIdentifier;
            if (resourceIdentifier.hasElementIdentifier()) {
                fullResourceIdentifier = resourceIdentifier;
            } else {
                fullResourceIdentifier = resourceIdentifier.extend(parent);
            }

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
            for (final String extension : fullResourceIdentifier.extensions()) {
                final ResourceIdentifier extendedIdentifier = fullResourceIdentifier.extend(extension);

                final Optional<GroupResolver> resolver = groupResolvers.stream().filter(r -> Arrays.equals(r.hierarchy(), extendedIdentifier.completeHierarchy())).findAny();
                if (resolver.isPresent()) {
                    final ApiResponse apiResponse = getResponseForResourceIdentifier(extendedIdentifier);
                    if (apiResponse != null && apiResponse.getObject() != null) {
                        objectBuilder.add(extension, apiResponse.getObject());
                    }
                }
            }

            final List<String> availableSubgroups = getAvailableSubResolver(fullResourceIdentifier, groupResolvers);
            final List<String> availableExtensions = getAvailableSubResolver(fullResourceIdentifier, extensionResolvers);
            if (!availableSubgroups.isEmpty() || !availableExtensions.isEmpty()) {
                objectBuilder.add("links", getLinks(fullResourceIdentifier, availableSubgroups, availableExtensions));
            }

            return objectBuilder.build();
        }
    }

}
