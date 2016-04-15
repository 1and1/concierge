package net.oneandone.concierge;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.oneandone.concierge.configuration.ApiGatewayConfiguration;
import net.oneandone.concierge.resource.GenericApiResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@SuppressWarnings("WeakerAccess")
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Concierge extends Application<ApiGatewayConfiguration> {

    private Environment environment;

    @Override
    public void run(final ApiGatewayConfiguration configuration, final Environment environment) throws Exception {
        environment.jersey().register(new GenericApiResource(configuration.getResolvers()));
        this.environment = environment;
    }

    /**
     * Starts the concierge web server with the specified configuration.
     * <p />
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
