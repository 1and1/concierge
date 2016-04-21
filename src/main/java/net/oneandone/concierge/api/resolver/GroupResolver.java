package net.oneandone.concierge.api.resolver;

import com.google.common.collect.ImmutableList;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.Filter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.filter.ShowFilter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Resolves all elements for a group. */
public interface GroupResolver extends Resolver {

    /**
     * The default page size or 0 if page size is unlimited.
     *
     * @return default page size or 0 if page size is unlimited
     */
    int defaultPageSize();

    /**
     * The miaxmum page size or 0 if page size is unlimited.
     *
     * @return miaxmum page size or 0 if page size is unlimited
     */
    int maximumPageSize();

    default List<Class<? extends Filter>> supportedFilterClasses() {
        if (defaultPageSize() != 0) {
            return ImmutableList.of(PageFilter.class, ShowFilter.class);
        }

        return ImmutableList.of(ShowFilter.class);
    }

    /**
     * Returns the group.
     *
     * @param parent the parent element
     * @param filters the list of filters
     * @return the group
     */
    Group elements(final Element parent, final Filters filters);

}
