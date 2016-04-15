package net.oneandone.concierge.example.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.example.model.Author;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthorResolver implements GroupResolver {
    @Override
    public int defaultPageSize() {
        return 2;
    }

    @Override
    public int maximumPageSize() {
        return 100;
    }

    @Override
    public Group elements(final Element parent, final Filters filters) {
        final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
        if (addressFilter.isPresent()) {
            final Optional<Author> author = Library.getAuthors().stream().filter(a -> a.address().equals(addressFilter.get().getAddress())).findAny();
            if (author.isPresent()) {
                return Group.withElement(author.get());
            } else {
                return Group.empty(name());
            }

        } else {
            final Optional<PageFilter> pageFilter = filters.get(PageFilter.class);

            int page = 1;
            int perPage = defaultPageSize();
            if (pageFilter.isPresent()) {
                page = pageFilter.get().getPage();
                perPage = pageFilter.get().getPerPage().orElse(perPage);
            }

            final int skip = (page - 1) * perPage;
            final int limit = perPage;
            return Group.withElements(name(), Library.getAuthors().stream().skip(skip).limit(limit).collect(Collectors.toList()), Library.getAuthors().size(), ZonedDateTime.now());
        }
    }

    @Override
    public String[] hierarchy() {
        return new String[] { "authors" };
    }
}
