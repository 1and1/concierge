package net.oneandone.concierge.example;

import net.oneandone.concierge.Concierge;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.api.resolver.Resolver;
import net.oneandone.concierge.example.resolver.AuthorResolver;
import net.oneandone.concierge.example.resolver.BookRatingExtensionResolver;
import net.oneandone.concierge.example.resolver.BookResolver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * Executes the service on {@code http://localhost:8080/}.
     * <p />
     * All resolvers are located within the package {@link net.oneandone.concierge.example.resolver}.
     * <p />
     * The example data for the resolvers is available in the {@link net.oneandone.concierge.example.resolver.Library}.
     *
     * @param args no required arguments
     * @throws Exception thrown if execution of server failed
     */
    public static void main(final String... args) throws Exception {
        final Resolver[] resolvers = new Resolver[] {
                new AuthorResolver(), new BookResolver(), new BookRatingExtensionResolver()
        };
        Concierge.prepare().port(8080).start(resolvers);
    }

}
