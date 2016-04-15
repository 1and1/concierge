package net.oneandone.concierge.api.filter;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** This class contains an array of {@link Filter} instances that can be applied on {@link net.oneandone.concierge.api.Group}s. */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Filters {

    /** Filters for {@code this} instance. */
    private final Filter[] filters;

    /**
     * Returns an optional filter for the requested filter class.
     *
     * @param filterClass the requested filter class
     * @param <T> the filter type
     * @return an optional filter
     */
    public <T extends Filter> Optional<T> get(final Class<T> filterClass) {
        Preconditions.checkNotNull(filterClass, "request filter class may not be null");

        for (final Filter filter : filters) {
            if (filter.getClass().equals(filterClass)) {
                return Optional.of(filterClass.cast(filter));
            }
        }
        return Optional.empty();
    }

    /**
     * The filter count.
     *
     * @return the filter count
     */
    public int size() {
        return filters.length;
    }

    /** Builder for {@link Filters}. */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        /** The list of filters. */
        private List<Filter> filters = new ArrayList<>();

        /**
         * Initializes a builder instance.
         *
         * @return a builder
         */
        public static Builder initialize() {
            return new Builder();
        }

        /**
         * Adds the specified filter and returns {@code this} instance.
         *
         * @param filter the filter to add
         * @return {@code this} instance
         */
        public Builder add(final Filter filter) {
            Preconditions.checkNotNull(filter, "tried to add filter null");
            Preconditions.checkArgument(
                    filters.stream().noneMatch(addedFilter -> addedFilter.getClass().equals(filter.getClass())),
                    "filter with class '" + filter.getClass().getSimpleName() + "' already added"
            );
            filters.add(filter);
            return this;
        }

        /**
         * Builds and returns a {@link Filters} instance.
         *
         * @return the filters
         */
        public Filters build() {
            return new Filters(filters.toArray(new Filter[filters.size()]));
        }

    }
}
