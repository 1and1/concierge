package net.oneandone.concierge.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.resource.response.ApiResourcePaging;
import net.oneandone.concierge.resource.response.ApiResponse;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenericApiResource {

    private List<ExtensionResolver> extensionResolvers = new ArrayList<>();
    private List<GroupResolver> groupResolvers = new ArrayList<>();

    public GenericApiResource(final List<GroupResolver> groupResolvers, final List<ExtensionResolver> extensionResolvers) {
        Preconditions.checkNotNull(groupResolvers, "the group resolvers may not be null");
        Preconditions.checkNotNull(extensionResolvers, "the extension resolvers may not be null");

        this.groupResolvers = groupResolvers;
        this.extensionResolvers = extensionResolvers;
    }

    @GET
    @Path("/{uri:.*}")
    public Response getResource(@PathParam("uri") String uri,
                                @QueryParam("page") Integer page,
                                @QueryParam("per_page") Integer perPage,
                                @QueryParam("show") List<String> show) {
        final ApiResponse apiResponse = getResponse(uri, Optional.ofNullable(page), Optional.ofNullable(perPage), show.toArray(new String[show.size()]));
        final JsonStructure jsonResponse = apiResponse.getObject();
        if (jsonResponse != null) {
            final Response.ResponseBuilder responseBuilder = Response.ok(jsonResponse.toString());
            if (apiResponse.getPaging().isPresent()) {
                responseBuilder.header("Accept-Ranges", apiResponse.getPaging().get().getAcceptRanges());
                responseBuilder.header("Content-Range", apiResponse.getPaging().get().getContentRange());
            }
            responseBuilder.header("Last-Modified", apiResponse.getLastModified().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            return responseBuilder.build();
        }
        return Response.serverError().entity("nothing found for " + uri).build();
    }

    /**
     * Creates a response for the specified url.
     *
     * @param url the URL
     * @param page the optional page
     * @param perPage the optional per page limit
     * @param extensions an array of all requested extensions
     * @return the response or null
     */
    private ApiResponse getResponse(final String url, final Optional<Integer> page, final Optional<Integer> perPage, final String... extensions) {
        Preconditions.checkNotNull(url, "the URI may not be null");
        Preconditions.checkArgument(!url.startsWith("/"), "the URI may not start with slash");

        final String[] urlParts = url.split("/");
        return getResponse(urlParts, null, page, perPage, extensions);
    }

    /**
     * Returns the JSON representation for the specified {@code addresses} wrapped in a {@link ApiResponse} or {@code null} if no resource could be found for the specified address.
     *
     * @param addresses the array of addresses
     * @param parent the parent element or {@code null} at the root of the resource graph
     * @param page the optional page
     * @param perPage the optional per page limit
     * @param extensions an array of all requested extensions
     * @return the response or {@code null}
     */
    private ApiResponse getResponse(final String[] addresses, final Element parent, Optional<Integer> page, Optional<Integer> perPage, final String... extensions) {
        Preconditions.checkNotNull(addresses, "the addresses may not be null");
        Preconditions.checkArgument(addresses.length > 0, "the addresses may not be empty");

        final String groupName = addresses[0];
        final Optional<GroupResolver> resolver = groupResolvers.stream().filter(r -> r.name().equals(groupName)).findAny();

        if (resolver.isPresent()) {
            return getGroupResponse(addresses, parent, page, perPage, groupName, resolver.get(), extensions);
        } else {
            final Optional<ExtensionResolver> extensionResolver = extensionResolvers.stream().filter(r -> r.name().equals(groupName)).findAny();
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
     * @param addresses the array of addresses
     * @param parent the parent element or {@code null} at the root of the resource graph
     * @param page the optional page
     * @param perPage the optional per page limit
     * @param groupName the group name within the selection
     * @param resolver the resolver for the group with the specified {@code groupName}
     * @param extensions an array of all requested extensions
     * @return the response
     */
    private ApiResponse getGroupResponse(String[] addresses, Element parent, Optional<Integer> page, Optional<Integer> perPage, String groupName, GroupResolver resolver, String... extensions) {
        // Unique by URL concept: group/element/group/element/...
        final boolean uniqueElement = addresses.length >= 2;

        final Filters filters = initializeFilters(addresses, page, perPage);
        final Group group = resolver.elements(parent, filters);

        final Multimap<Element, Extension> extensionMultimap = HashMultimap.create();
        for (final String extension : extensions) {
            final List<ExtensionResolver> extensionResolvers = this.extensionResolvers.stream().filter(e -> e.name().equals(extension) && e.forGroup().equals(addresses[0])).collect(Collectors.toList());
            for (final ExtensionResolver extensionResolver : extensionResolvers) {
                final Map<Element, Extension> resolvedExtensions = extensionResolver.resolve(group);
                for (final Map.Entry<Element, Extension> elementExtensionEntry : resolvedExtensions.entrySet()) {
                    extensionMultimap.put(elementExtensionEntry.getKey(), elementExtensionEntry.getValue());
                }
            }
        }

        final Optional<String[]> nextUrl;
        if (addresses.length > 2) {
            nextUrl = Optional.of(Arrays.copyOfRange(addresses, 2, addresses.length));
        } else {
            nextUrl = Optional.empty();
        }

        if (uniqueElement) {
            final Element element = group.elements().get(0);
            return ApiResponse.create(getExtendedJsonStructure(nextUrl, element, extensions, extensionMultimap.get(element)), element.lastModified());
        } else {
            final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (final Element element : group.elements()) {
                arrayBuilder.add(getExtendedJsonStructure(nextUrl, element, extensions, extensionMultimap.get(element)));
            }

            if (resolver.defaultPageSize() > 0) {
                final ApiResourcePaging paging = ApiResourcePaging.builder().group(groupName).page(page.orElse(1)).perPage(perPage.orElse(resolver.defaultPageSize())).total(group.total()).build();
                return ApiResponse.create(arrayBuilder.build(), group.lastModified(), paging);
            }
            return ApiResponse.create(arrayBuilder.build(), group.lastModified());
        }
    }

    /**
     * Returns the {@link JsonStructure} for the parent element if {@code nextUrl} is empty or else forwards the request to
     * {@link #getResponse(String[], Element, Optional, Optional, String...)} with the current parent element and selects recursively the result of it.
     *
     * @param nextUrl the optional nexty url
     * @param parent the parent element
     * @param extensions an array of all requested extensions
     * @param resolvedExtensions a list of all resolved extensions so far
     * @return the JSON representation for the parent element or the result of a forwarded request
     */
    private JsonStructure getExtendedJsonStructure(final Optional<String[]> nextUrl, final Element parent, String[] extensions, final Collection<Extension> resolvedExtensions) {
        if (nextUrl.isPresent()) {
            return getResponse(nextUrl.get(), parent, Optional.empty(), Optional.empty(), extensions).getObject();
        } else {
            final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            final JsonObject jsonObject = parent.content();
            for (final String key : jsonObject.keySet()) {
                objectBuilder.add(key, jsonObject.get(key));
            }

            for (final Extension extension : resolvedExtensions) {
                objectBuilder.add(extension.address(), extension.content());
            }

            for (final String extension : extensions) {
                final Optional<GroupResolver> resolver = groupResolvers.stream().filter(r -> r.name().equals(extension) && r.parentGroup().isPresent() && parent.group().equals(r.parentGroup().get())).findAny();
                if (resolver.isPresent()) {
                    final ApiResponse apiResponse = getResponse(new String[]{extension}, parent, Optional.empty(), Optional.empty(), extensions);
                    if (apiResponse != null) {
                        objectBuilder.add(extension, apiResponse.getObject());
                    }
                }
            }

            return objectBuilder.build();
        }
    }

    /**
     * Returns a list of all {@link Filter} for the specified {@code resolver}.
     *
     * @param addresses the addresses array
     * @param page the optional page
     * @param perPage the optional per page limit
     * @return th elist of all filters specified for the resolver
     */
    private Filters initializeFilters(String[] addresses, Optional<Integer> page, Optional<Integer> perPage) {
        final Filters.Builder builder = Filters.Builder.initialize();

        if (page.isPresent() || perPage.isPresent()) {
            builder.add(new PageFilter(page.orElse(1), perPage));
        }
        if (addresses.length > 1) {
            builder.add(new AddressFilter(addresses[1]));
        }
        return builder.build();
    }

}
