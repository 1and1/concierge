package net.oneandone.concierge;

import com.google.common.base.Preconditions;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.configuration.ApiGatewayConfiguration;
import net.oneandone.concierge.resource.GenericApiResource;

import java.net.URL;

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
 * These resolvers will be dynamically created by the configuration specified in {@link #start(URL)}.
 * As this implementation is based on <a href="http://www.dropwizard.io/docs/">Dropwizard</a>, you can
 * learn more about the configuration file format in their <a href="http://www.dropwizard.io/0.9.2/docs/manual/configuration.html">
 * user manual</a>.
 * <p />
 * The additional properties for the configuration file are defined in {@link ApiGatewayConfiguration}. Basically
 * these properties are list of class names for the resolver implementations you want to use.
 * <p />
 * You'll find an example implementation on <a href="https://github.com/1and1/concierge/tree/master/concierge-example-server">GitHub</a>.
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Concierge extends Application<ApiGatewayConfiguration> {

    /** The server environment. */
    private Environment environment;

    @Override
    public void run(final ApiGatewayConfiguration configuration, final Environment environment) throws Exception {
        environment.jersey().register(new GenericApiResource(configuration.getResolvers()));
        this.environment = environment;
    }

    /**
     * Starts the concierge web server with the specified configuration.
     * <p/>
     * The call of this method is not blocking.
     *
     * @param configurationFile the configuration file URL
     * @return the concierge web server instance
     * @throws Exception thrown on any exception
     */
    public static Concierge start(final URL configurationFile) throws Exception {
        Preconditions.checkNotNull(configurationFile, "the configuration file URL may not be null");

        log.info("configuration file URL is {}", configurationFile);
        final Concierge concierge = new Concierge();
        concierge.run("server", configurationFile.getFile());
        return concierge;
    }

    /**
     * Stops this concierge web server instance.
     *
     * @throws IllegalStateException if the server wasn't started
     */
    public void stop() throws Exception {
        if (environment != null) {
            log.debug("about to stop application");
            environment.getApplicationContext().getServer().stop();
        } else {
            throw new IllegalStateException("environment may not be null on close");
        }
    }
}
