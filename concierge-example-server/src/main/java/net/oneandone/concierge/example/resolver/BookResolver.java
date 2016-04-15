package net.oneandone.concierge.example.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.example.model.Author;
import net.oneandone.concierge.example.model.Book;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookResolver implements GroupResolver {
    @Override
    public int defaultPageSize() {
        return 0;
    }

    @Override
    public int maximumPageSize() {
        return 0;
    }

    @Override
    public Group elements(final Element parent, final Filters filters) {
        final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
        if (addressFilter.isPresent()) {
            final Optional<Book> book = Library.getBooks((Author) parent).stream().filter(a -> a.address().equals(addressFilter.get().getAddress())).findAny();
            if (book.isPresent()) {
                return Group.withElement(book.get());
            } else {
                return Group.empty(name());
            }
        } else {
            return Group.withElements(name(), Library.getBooks((Author) parent).stream().collect(Collectors.toList()), Library.getAuthors().size(), ZonedDateTime.now());
        }
    }

    @Override
    public String[] hierarchy() {
        return new String[] { "authors", "books" };
    }
}
