package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.Filters;

import java.util.Optional;

/** Resolves all elements for a group. */
public interface GroupResolver extends Resolver {

    /**
     * Returns the optionally present parent group.
     * E.g. the {@code interfaces} parent group is the group class of {@code devices}.
     *
     * @return the optionally present parent group
     */
    public Optional<String> parentGroup();

    /**
     * The default page size or 0 if page size is unlimited.
     *
     * @return default page size or 0 if page size is unlimited
     */
    public int defaultPageSize();

    /**
     * The miaxmum page size or 0 if page size is unlimited.
     *
     * @return miaxmum page size or 0 if page size is unlimited
     */
    public int maximumPageSize();

    /**
     * Returns the group.
     *
     * @param parent the parent element
     * @param filters the list of filters
     * @return the group
     */
    public Group elements(final Element parent, final Filters filters);

}
