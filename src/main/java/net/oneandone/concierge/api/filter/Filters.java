package net.oneandone.concierge.api.filter;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Filters {

    private final Filter[] filters;

    public <T extends Filter> Optional<T> get(final Class<T> filterClass) {
        Preconditions.checkNotNull(filterClass, "request filter class may not be null");

        for (final Filter filter : filters) {
            if (filter.getClass().equals(filterClass)) {
                return Optional.of(filterClass.cast(filter));
            }
        }
        return Optional.empty();
    }

    public int size() {
        return filters.length;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private List<Filter> filters = new ArrayList<>();

        public static Builder initialize() {
            return new Builder();
        }

        public Builder add(final Filter filter) {
            Preconditions.checkNotNull(filter, "tried to add filter null");
            Preconditions.checkArgument(
                    filters.stream().noneMatch(addedFilter -> addedFilter.getClass().equals(filter.getClass())),
                    "filter with class '" + filter.getClass().getSimpleName() + "' already added"
            );
            filters.add(filter);
            return this;
        }

        public Filters build() {
            return new Filters(filters.toArray(new Filter[filters.size()]));
        }

    }
}
