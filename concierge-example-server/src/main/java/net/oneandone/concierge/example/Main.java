package net.oneandone.concierge.example;

import net.oneandone.concierge.Concierge;

import java.net.URL;

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
        final URL configurationURL = ClassLoader.getSystemResource("server.yml");
        Concierge.start(configurationURL);
    }

}
