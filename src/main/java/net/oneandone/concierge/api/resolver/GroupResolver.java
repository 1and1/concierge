package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.Filters;

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

    /**
     * Returns the group.
     *
     * @param parent  the parent element
     * @param filters the list of filters
     * @return the group
     */
    Group elements(final Element parent, final Filters filters);

}
