package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public abstract class BasicGroupResolver implements GroupResolver {

    public abstract Optional<Element> element(final Element parent, final String address);
    public abstract int total(final Filters filters);
    public abstract ZonedDateTime lastUpdate(final Filters filters);
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

        return Group.withElements(name(), elements(parent, page, perPage, filters), total(filters), lastUpdate(filters));
    }

}
