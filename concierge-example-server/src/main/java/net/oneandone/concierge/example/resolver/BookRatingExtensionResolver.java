package net.oneandone.concierge.example.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.example.model.Book;

import java.util.Optional;

public class BookRatingExtensionResolver implements ExtensionResolver {
    @Override
    public Optional<Extension> resolve(final Element element) {
        return Library.getRating((Book) element).map(e -> (Extension) e);
    }

    @Override
    public String[] hierarchy() {
        return new String[] { "authors", "books", "rating" };
    }
}
