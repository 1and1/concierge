package net.oneandone.concierge.example.resolver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.oneandone.concierge.example.model.Author;
import net.oneandone.concierge.example.model.Book;

import java.util.Collection;

public class Library {

    private static final Multimap<Author, Book> LIBRARY = HashMultimap.create();
    private static boolean initialized = false;

    protected static Collection<Author> getAuthors() {
        initialize();
        return LIBRARY.keySet();
    }

    protected static Collection<Book> getBooks(final Author author) {
        initialize();
        return LIBRARY.get(author);
    }

    private static void initialize() {
        if (!initialized) {

            final Author tolkien = new Author(1L, "J. R. R. Tolkien");
            LIBRARY.put(tolkien, new Book(1L, "The Lord of the Rings"));
            LIBRARY.put(tolkien, new Book(2L, "The Hobbit"));
            LIBRARY.put(tolkien, new Book(3L, "The Silmarillion"));

            final Author martin = new Author(2L, "George R. R. Martin");
            LIBRARY.put(martin, new Book(4L, "A Song of Ice and Fire"));

            final Author kirkman = new Author(3L, "Robert Kirkman");
            LIBRARY.put(kirkman, new Book(5L, "The Walking Dead"));
            LIBRARY.put(kirkman, new Book(6L, "Invincible"));

            initialized = true;
        }
    }

}
