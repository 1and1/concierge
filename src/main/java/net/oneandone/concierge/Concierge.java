package net.oneandone.concierge;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.api.resolver.Resolver;
import net.oneandone.concierge.resource.GenericApiResource;
import spark.Spark;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * The {@code Concierge} will help you to create an API gateway easily.
 * <p/>
 * The idea is pretty simple. We defined a resource identifier pattern which you can learn in the documentation of the
 * {@link net.oneandone.concierge.resource.ResourceIdentifier}. As you will learn, there are three kinds of resources:
 * <ul>
 * <li>{@link Element elements}</li>
 * <li>{@link net.oneandone.concierge.api.Group groups}</li>
 * <li>and {@link net.oneandone.concierge.api.Extension extensions}</li>
 * </ul>
 * To resolve these resources you can implement the {@link net.oneandone.concierge.api.resolver.Resolver resolvers}.
 * There are {@link net.oneandone.concierge.api.resolver.GroupResolver} to resolve elements, and
 * {@link net.oneandone.concierge.api.resolver.ExtensionResolver} to resolve extensions for elements.
 * <p />
 * You can setup a server and initialize it with your resolvers by running {@link Concierge#prepare()}.
 * <p />
 * You'll find an example implementation on <a href="https://github.com/1and1/concierge/tree/master/concierge-example-server">GitHub</a>.
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class Concierge {

    private static final Object MUTEX = new Object();
    private static boolean active = false;

    private Concierge(final List<GroupResolver> groupResolvers, final List<ExtensionResolver> extensionResolvers) {
        GenericApiResource server = new GenericApiResource(groupResolvers, extensionResolvers);

        Spark.get("*", server::getGetResponse);
        Spark.options("*", server::getOptionsResponse);
    }

    /**
     * Stops this concierge web server instance.
     *
     * @throws IllegalStateException if the server wasn't started
     */
    public void stop() throws Exception {
        log.debug("about to stop application");
        Spark.stop();
        synchronized (MUTEX) {
            Concierge.active = false;
        }
    }

    public static ConciergeBuilder prepare() {
        return new ConciergeBuilder();
    }

    public static class ConciergeBuilder {

        private OptionalInt port = OptionalInt.empty();

        private ConciergeBuilder () {}

        public ConciergeBuilder port(final int port) {
            Preconditions.checkArgument(port > 0 && port <= Short.MAX_VALUE, "specified port " + port + " is not within allowed range [0, " + Short.MAX_VALUE + "]");
            this.port = OptionalInt.of(port);
            return this;
        }

        public Concierge start(final Resolver... resolvers) throws Exception {
            synchronized (MUTEX) {
                if (!active) {
                    if (port.isPresent()) {
                        Spark.port(port.getAsInt());
                    }

                    final List<GroupResolver> groupResolvers = Arrays.stream(resolvers)
                            .filter(resolver -> resolver instanceof GroupResolver)
                            .map(resolver -> (GroupResolver) resolver)
                            .collect(Collectors.toList());

                    final List<ExtensionResolver> extensionResolvers = Arrays.stream(resolvers)
                            .filter(resolver -> resolver instanceof ExtensionResolver)
                            .map(resolver -> (ExtensionResolver) resolver)
                            .collect(Collectors.toList());

                    final Concierge concierge = new Concierge(groupResolvers, extensionResolvers);
                    Concierge.active = true;
                    return concierge;
                }
                throw new IllegalStateException("server is already running in context");
            }
        }

    }
}
