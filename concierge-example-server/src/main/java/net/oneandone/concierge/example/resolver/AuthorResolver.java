package net.oneandone.concierge.example.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.resolver.BasicGroupResolver;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthorResolver extends BasicGroupResolver {

    @Override
    public int defaultPageSize() {
        return 3;
    }

    @Override
    public int maximumPageSize() {
        return 100;
    }

    @Override
    public String[] hierarchy() {
        return new String[] { "authors" };
    }

    @Override
    public Optional<Element> element(final Element parent, final String address) {
        return Library.getAuthors().stream().map(a -> (Element) a).filter(a -> a.address().equals(address)).findAny();
    }

    @Override
    public int total(final Element parent, final Filters filters) {
        return Library.getAuthors().size();
    }

    @Override
    public ZonedDateTime lastUpdate(final Element parent, final Filters filters) {
        return ZonedDateTime.now();
    }

    @Override
    public List<Element> elements(final Element parent, final int page, final int perPage, final Filters filters) {
        final int skip = (page - 1) * perPage;
        final int limit = perPage;
        return Library.getAuthors().stream().skip(skip).limit(limit).collect(Collectors.toList());
    }

}
