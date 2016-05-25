package net.oneandone.concierge.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.OptionalInt;

/**
 * This class implements our resource identifier pattern.
 * <p/>
 * We have three types of resources:
 * <ol>
 * <li>{@link Element elements} are uniquely addressable resources</li>
 * <li>{@link net.oneandone.concierge.api.Group groups} may contain multiple elements</li>
 * <li>{@link net.oneandone.concierge.api.Extension extensions} may provided additional information for elements</li>
 * </ol>
 * Elements may also contain further groups, which leads us to a pattern how to address resources.
 * <ul>
 * <li>A resource identifier always starts with a group and may end anytime</li>
 * <li>If something follows on a group it's always an element</li>
 * <li>The next identifier to an element might be the name of a group or extension</li>
 * <li>An extension must terminate the resource identifier</li>
 * </ul>
 * So a resource identifier might look like {@code group1/id1/group2/id2/extension}.
 * <p/>
 * The scope of a resource identifier is a pair of group and an optional element identifier or the address of an extension.
 * In the example above we see three scopes:
 * <ol>
 * <li>{@code group1/id1}</li>
 * <li>{@code group2/id2}</li>
 * <li>and {@code extension}</li>
 * </ol>
 * The resource identifier will be initialized with scope 1) and will return the scope on call of this method.
 */
@SuppressWarnings("WeakerAccess")
public class ResourceIdentifier {

    /** The complete URI. */
    private final String[] uri;

    /** The start index of the current scope (see {@link #next()}. */
    private final int startIndex;

    /** The request parameters. */
    private final Multimap<String, String> parameters;

    private ResourceIdentifier(final String uri, final Multimap<String, String> parameters) {
        Preconditions.checkNotNull(uri, "the URI may not be null");
        Preconditions.checkArgument(!uri.startsWith("/"), "the URI may not start with slash");
        if (uri.isEmpty()) {
            this.uri = new String[0];
        } else {
            this.uri = uri.split("/");
        }
        this.parameters = ImmutableMultimap.copyOf(parameters);
        this.startIndex = 0;
    }

    private ResourceIdentifier(final String[] uri, final Multimap<String, String> parameters, final int startIndex) {
        this.uri = uri;
        this.parameters = parameters;
        this.startIndex = startIndex;
    }

    /**
     * Parses the specified URI and returns the resource identifier.
     *
     * @param uri the URI to parse
     * @return the resource identifier for the specified URI
     */
    public static ResourceIdentifier parse(final String uri) {
        return new ResourceIdentifier(uri, ImmutableMultimap.of());
    }

    /**
     * Parses the specified URI and returns the resource identifier.
     *
     * @param uri        the URI to parse
     * @param parameters the request parameters
     * @return the resource identifier for the specified URI
     */
    public static ResourceIdentifier parse(final String uri, final Multimap<String, String> parameters) {
        return new ResourceIdentifier(uri, parameters);
    }

    /**
     * Returns a copy of the complete parsed URI.
     *
     * @return the complete parsed URI
     */
    public String[] get() {
        return Arrays.copyOf(uri, uri.length);
    }

    /**
     * Returns {@code true} if this resource identifier is empty, otherwise {@code false}.
     *
     * @return {@code true} if this resource identifier is empty, otherwise {@code false}
     */
    public boolean empty() {
        return uri.length == 0;
    }

    /**
     * Returns the hierarchy for the current scope.
     * <p/>
     * The scope is initially set to the root of the URI and will be iterated within the result of each call on {@link #next()}.
     *
     * @return the hierarchy for the current scope
     * @see #completeHierarchy()
     */
    public String[] hierarchy() {
        final String[] resolverHierarchy = new String[(startIndex / 2) + 1];
        for (int i = 0; i <= startIndex; i += 2) {
            resolverHierarchy[i / 2] = uri[i];
        }
        return resolverHierarchy;
    }

    /**
     * Extends the hierarchy of the current scope by the specified extensions and returns it.
     *
     * @param extension the extension name
     * @return the extended hierarchy for the current scope
     * @see #hierarchy()
     */
    public String[] extendedHierarchy(final String extension) {
        final String[] resolverHierarchy = hierarchy();

        final String[] extendedResolverHierarchy = Arrays.copyOfRange(resolverHierarchy, 0, resolverHierarchy.length + 1);
        extendedResolverHierarchy[resolverHierarchy.length] = extension;
        return extendedResolverHierarchy;
    }

    /**
     * Returns a copy the complete group and extension hierarchy.
     * <p/>
     * An URI of {@code group1/id1/group2/id2/extension} will return {@code group1/group2/extension}.
     *
     * @return the complete group and extension hierarchy
     */
    public String[] completeHierarchy() {
        final String[] resolverHierarchy = uri.length % 2 == 0 ? new String[uri.length / 2] : new String[uri.length / 2 + 1];
        for (int i = 0; i < uri.length; i += 2) {
            resolverHierarchy[i / 2] = uri[i];
        }
        return resolverHierarchy;
    }

    /**
     * Returns {@code true} if their are more scopes available, otherwise {@code false}.
     *
     * @return {@code true} if their are more scopes available, otherwise {@code false}
     */
    public boolean hasNextScope() {
        return startIndex + 2 < uri.length;
    }

    /**
     * Returns the resource identifier for the next scope.
     * <p/>
     * The scope is a pair of group and an optional element identifier or the address of an extension.
     * E.g the URI {@code group1/id1/group2/id2/extension} has three scopes:
     * <ol>
     * <li>{@code group1/id1}</li>
     * <li>{@code group2/id2}</li>
     * <li>and {@code extension}</li>
     * </ol>
     * The resource identifier will be initialized with scope 1) and will return the scope on call of this method.
     *
     * @return the resource identifier for the next scope
     */
    public ResourceIdentifier next() {
        if (hasNextScope()) {
            return new ResourceIdentifier(uri, parameters, startIndex + 2);
        }
        throw new IndexOutOfBoundsException("next resource identifier is not available");
    }

    /**
     * Returns {@code true} if there is an element identifier in the current scope, otherwise {@code false}.
     *
     * @return {@code true} if there is an element identifier in the current scope, otherwise {@code false}
     */
    public boolean hasElementIdentifier() {
        return startIndex + 1 <= uri.length - 1;
    }

    /**
     * Returns the group identifier.
     *
     * @return the group identifier
     */
    public String groupIdentifier() {
        return uri[startIndex];
    }

    /**
     * Returns the element identifier.
     *
     * @return the element identifier
     * @throws IndexOutOfBoundsException if no element identifier is available, see {@link #hasElementIdentifier()}
     */
    private String elementIdentifier() {
        if (hasElementIdentifier()) {
            return uri[startIndex + 1];
        }
        throw new IndexOutOfBoundsException("resource identifier has no element identifier");
    }

    /**
     * Extends the current resource identifier by the specified extension.
     * <p/>
     * Extensions are only available for elements. If the extend operation will be called
     * for a group or an extension an {@link IllegalArgumentException} will be thrown instead.
     *
     * @param extension the extension name
     * @return the extended resource identifier
     */
    public ResourceIdentifier extend(final String extension) {
        if (hasElementIdentifier()) {
            final String[] newUri = Arrays.copyOfRange(uri, 0, startIndex + 3);
            newUri[startIndex + 2] = extension;
            return new ResourceIdentifier(newUri, parameters, startIndex);
        }
        throw new IllegalArgumentException("resource identifier is not extensible");
    }

    /**
     * Extends the current resource identifier by the specified element {@link Element#address()}.
     * <p/>
     * The current resource identifier can only be extended if no element identifier (see {@link #hasElementIdentifier()}).
     * If this resource identifier should be extended by an element and an element identifier already exists an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param element the element to use for the extension
     * @return the extended resource identifier
     */
    public ResourceIdentifier extend(final Element element) {
        if (!hasElementIdentifier()) {
            final String[] newUri = Arrays.copyOfRange(uri, 0, startIndex + 2);
            newUri[startIndex + 1] = element.address();
            return new ResourceIdentifier(newUri, parameters, startIndex);
        }
        throw new IllegalArgumentException("current resource identifier " + Arrays.toString(uri) + " is not expendable by an element");
    }

    /**
     * Returns all extensions as specified by the {@code show} query parameter.
     *
     * @return collection of all requested extensions
     */
    public Collection<String> extensions() {
        return parameters.get("show");
    }

    /**
     * Returns the filters instance for the current scope of {@code this} resource identifier.
     * <p/>
     * See {@link #next()} to learn more about the scope.
     *
     * @return the filters
     */
    public Filters filters() {
        final Filters.Builder builder = Filters.Builder.initialize();
        if (hasElementIdentifier()) {
            builder.add(new AddressFilter(elementIdentifier()));
        }

        if (!hasNextScope() && (parameters.containsKey("page") || parameters.containsKey("per_page"))) {
            final Collection<String> pageValues = parameters.get("page");
            final int page;
            if (pageValues.isEmpty()) {
                page = 1;
            } else {
                page = Integer.parseInt(pageValues.stream().findFirst().get());
            }

            final Collection<String> perPageValues = parameters.get("per_page");
            final OptionalInt perPage;
            if (perPageValues.isEmpty()) {
                perPage = OptionalInt.empty();
            } else {
                perPage = OptionalInt.of(Integer.parseInt(perPageValues.stream().findFirst().get()));
            }
            builder.add(new PageFilter(page, perPage));
        }

        return builder.build();
    }
}
