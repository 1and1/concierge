package net.oneandone.concierge.resource.helper;

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

public class ResourceIdentifier {

    private final String[] uri;
    private final int startIndex;
    private final Multimap<String, String> parameters;

    private ResourceIdentifier(final String[] uri, final Multimap<String, String> parameters) {
        this.uri = uri;
        this.startIndex = 0;
        this.parameters = parameters;
    }

    private ResourceIdentifier(final String[] uri, final Multimap<String, String> parameters, final int startIndex) {
        this.uri = uri;
        this.parameters = parameters;
        this.startIndex = startIndex;
    }

    public static ResourceIdentifier parse(final String uri) {
        Preconditions.checkNotNull(uri, "the URI may not be null");
        Preconditions.checkArgument(!uri.startsWith("/"), "the URI may not start with slash");
        if (uri.isEmpty()) {
            return new ResourceIdentifier(new String[0], ImmutableMultimap.of());
        }
        return new ResourceIdentifier(uri.split("/"), ImmutableMultimap.of());
    }

    public static ResourceIdentifier parse(final String uri, final Multimap<String, String> parameters) {
        Preconditions.checkNotNull(uri, "the URI may not be null");
        Preconditions.checkArgument(!uri.startsWith("/"), "the URI may not start with slash");
        if (uri.isEmpty()) {
            return new ResourceIdentifier(new String[0], ImmutableMultimap.copyOf(parameters));
        }
        return new ResourceIdentifier(uri.split("/"), ImmutableMultimap.copyOf(parameters));
    }

    public String[] getCompleteIdentifier() {
        return Arrays.copyOf(uri, uri.length);
    }

    public boolean isEmpty() {
        return uri.length == 0;
    }

    public String[] getResolverHierarchy() {
        final String[] resolverHierarchy = new String[(startIndex / 2) + 1];
        for (int i = 0; i <= startIndex; i += 2) {
            resolverHierarchy[i / 2] = uri[i];
        }
        return resolverHierarchy;
    }

    public String[] getExtendedResolverHierrchy(final String extension) {
        final String[] resolverHierarchy = getResolverHierarchy();

        final String[] extendedResolverHierarchy = Arrays.copyOfRange(resolverHierarchy, 0, resolverHierarchy.length + 1);
        extendedResolverHierarchy[resolverHierarchy.length] = extension;
        return extendedResolverHierarchy;
    }

    public String[] getCompleteResolverHierarchy() {
        final String[] resolverHierarchy = uri.length % 2 == 0 ? new String[uri.length / 2] : new String[uri.length / 2 + 1];
        for (int i = 0; i < uri.length; i += 2) {
            resolverHierarchy[i / 2] = uri[i];
        }
        return resolverHierarchy;
    }

    public boolean isFinalPart() {
        return startIndex + 1 >= uri.length - 1;
    }

    public boolean hasNextPart() {
        return startIndex + 2 < uri.length;
    }

    public ResourceIdentifier getNextPart() {
        if (hasNextPart()) {
            return new ResourceIdentifier(uri, parameters, startIndex + 2);
        }
        throw new IndexOutOfBoundsException("next resource identifier is not available");
    }

    public boolean isElementIdentifier() {
        return startIndex + 1 <= uri.length - 1;
    }

    public String getGroupOrExtensionIdentifier() {
        return uri[startIndex];
    }

    private String getElementIdentifier() {
        return uri[startIndex + 1];
    }

    public boolean isExtensible() {
        return startIndex + 1 <= uri.length - 1;
    }

    public ResourceIdentifier getExtendedIdentifier(final String extension) {
        if (isExtensible()) {
            final String[] newUri = Arrays.copyOfRange(uri, 0, startIndex + 3);
            newUri[startIndex + 2] = extension;
            return new ResourceIdentifier(newUri, parameters, startIndex);
        }
        throw new IllegalStateException("resource identifier is not extensible");
    }

    public ResourceIdentifier extend(final Element element) {
        if (!isElementIdentifier()) {
            final String[] newUri = Arrays.copyOfRange(uri, 0, startIndex + 2);
            newUri[startIndex + 1] = element.address();
            return new ResourceIdentifier(newUri, parameters, startIndex);
        }
        return this;
    }

    public Collection<String> getExtensions() {
        return parameters.get("show");
    }

    public Filters getFilters() {
        final Filters.Builder builder = Filters.Builder.initialize();
        if (isElementIdentifier()) {
            builder.add(new AddressFilter(getElementIdentifier()));
        }

        if (isFinalPart()) {
            if (parameters.containsKey("page") || parameters.containsKey("per_page")) {
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
        }

        return builder.build();
    }
}
