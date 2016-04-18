package net.oneandone.concierge.example.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.resolver.BasicGroupResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.example.model.Author;
import net.oneandone.concierge.example.model.Book;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookResolver extends BasicGroupResolver {

    @Override
    public int defaultPageSize() {
        return 0;
    }

    @Override
    public int maximumPageSize() {
        return 0;
    }

    @Override
    public String[] hierarchy() {
        return new String[] { "authors", "books" };
    }

    @Override
    public Optional<Element> element(final Element parent, final String address) {
        return Library.getBooks((Author) parent).stream().map(b -> (Element) b).filter(a -> a.address().equals(address)).findAny();
    }

    @Override
    public int total(final Element parent, final Filters filters) {
        return Library.getBooks((Author) parent).size();
    }

    @Override
    public ZonedDateTime lastUpdate(final Element parent, final Filters filters) {
        return ZonedDateTime.now();
    }

    @Override
    public List<Element> elements(final Element parent, final int page, final int perPage, final Filters filters) {
        return Library.getBooks((Author) parent).stream().collect(Collectors.toList());
    }

}
