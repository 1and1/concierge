package net.oneandone.concierge.example.resolver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import net.oneandone.concierge.example.model.Author;
import net.oneandone.concierge.example.model.Book;
import net.oneandone.concierge.example.model.BookRating;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {

    /** Initialization mutex. */
    private static final Object MUTEX = new Object();

    /** The map of authors and their books. */
    private static final Multimap<Author, BookAndRating> LIBRARY = HashMultimap.create();

    /** Flag indicating whether the demo data was initialized in {@link #initialize()}. */
    private static boolean initialized = false;

    protected static Collection<Author> getAuthors() {
        initialize();
        return LIBRARY.keySet();
    }

    protected static Collection<Book> getBooks(final Author author) {
        initialize();
        return LIBRARY.get(author).stream().map(e -> e.book).collect(Collectors.toList());
    }

    protected static Optional<BookRating> getRating(final Book book) {
        initialize();
        return LIBRARY.values().stream().filter(e -> e.book.equals(book)).map(e -> e.bookRating).findAny();
    }

    private static void initialize() {
        if (!initialized) {
            synchronized (MUTEX) {
                if (!initialized) {
                    final Author tolkien = new Author(1L, "J. R. R. Tolkien", "British");
                    LIBRARY.put(tolkien, new BookAndRating(new Book(1L, "The Lord of the Rings"), new BookRating("4.46")));
                    LIBRARY.put(tolkien, new BookAndRating(new Book(2L, "The Hobbit"), new BookRating("4.23")));
                    LIBRARY.put(tolkien, new BookAndRating(new Book(3L, "The Silmarillion"), new BookRating("3.84")));

                    final Author martin = new Author(2L, "George R. R. Martin", "American");
                    LIBRARY.put(martin, new BookAndRating(new Book(4L, "A Song of Ice and Fire"), new BookRating("4.44")));

                    final Author kirkman = new Author(3L, "Robert Kirkman", "American");
                    LIBRARY.put(kirkman, new BookAndRating(new Book(5L, "The Walking Dead"), new BookRating("4.43")));
                    LIBRARY.put(kirkman, new BookAndRating(new Book(6L, "Invincible"), new BookRating("4.24")));

                    final Author goethe = new Author(4L, "Johann Wolfgang von Goethe", "German");
                    LIBRARY.put(goethe, new BookAndRating(new Book(7L, "Faust"), new BookRating("3.97")));
                    LIBRARY.put(goethe, new BookAndRating(new Book(8L, "The Sorrows of Young Werther"), new BookRating("3.61")));

                    initialized = true;
                }
            }
        }
    }

    @AllArgsConstructor
    static class BookAndRating {
        private final Book book;
        private final BookRating bookRating;
    }

}
