package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/** Implementation of a basic group resolver in order to prevent if-else cascades. */
public abstract class BasicGroupResolver implements GroupResolver {

    /**
     * Return a specific element for the specified address.
     *
     * @param parent  the parent element
     * @param address the address
     * @return the optional result
     */
    public abstract Optional<Element> element(final Element parent, final String address);

    /**
     * Returns the number of total elements for the specified parent and filters.
     *
     * @param parent  the parent element
     * @param filters the filters
     * @return the number of total elements
     */
    public abstract int total(final Element parent, final Filters filters);

    /**
     * Returns the last update date for the list with the specified parent and filters.
     *
     * @param parent  the parent element
     * @param filters the filters
     * @return the last update date
     */
    public abstract ZonedDateTime lastUpdate(final Element parent, final Filters filters);

    /**
     * Returns the list of elements for the specified parent and filters.
     * <p/>
     * The result may or may not be paged.
     *
     * @param parent  the parent element
     * @param page    the page or {@code 0} if paging is not required
     * @param perPage the per page count or {@code 0} if paging is not required
     * @param filters the filters
     * @return the list of elements
     */
    public abstract List<Element> elements(final Element parent, final int page, final int perPage, final Filters filters);

    @Override
    public final Group elements(final Element parent, final Filters filters) {
        final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
        if (addressFilter.isPresent()) {
            final Optional<? extends Element> element = element(parent, addressFilter.get().getAddress());
            if (element.isPresent()) {
                return Group.withElement(element.get());
            } else {
                return Group.empty(name());
            }
        }

        final Optional<PageFilter> pageFilter = filters.get(PageFilter.class);
        int page = 0;
        int perPage = defaultPageSize();
        if (defaultPageSize() != 0 && maximumPageSize() != 0) {
            page = 1;
            if (pageFilter.isPresent()) {
                page = pageFilter.get().getPage();
                perPage = pageFilter.get().getPerPage().orElse(perPage);
            }
        }

        return Group.withElements(name(), elements(parent, page, perPage, filters), total(parent, filters), lastUpdate(parent, filters));
    }

}
