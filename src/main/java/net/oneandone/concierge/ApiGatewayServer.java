package net.oneandone.concierge;

import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.configuration.ApiGatewayConfiguration;
import net.oneandone.concierge.resource.GenericApiResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Slf4j
public class ApiGatewayServer extends Application<ApiGatewayConfiguration> {

    private Environment environment;

    @Override
    public void run(final ApiGatewayConfiguration configuration, final Environment environment) throws Exception {
        final List<GroupResolver> groupResolvers = configuration.getResolvers().getGroupResolvers();
        final List<ExtensionResolver> extensionResolvers = configuration.getResolvers().getExtensionResolvers();

        environment.jersey().register(new GenericApiResource(groupResolvers, extensionResolvers));

        this.environment = environment;
    }

    public static ApiGatewayServer start(final URL configurationFile) throws Exception {
        log.info("configuration file URL is {}", configurationFile);
        final ApiGatewayServer apiGatewayServer = new ApiGatewayServer();
        apiGatewayServer.run("server", configurationFile.getFile());
        return apiGatewayServer;
    }


    public void stop() throws Exception {
        if (environment != null) {
            log.debug("about to stop application");
            environment.getApplicationContext().getServer().stop();
        } else {
            throw new IllegalStateException("environment may not be null on close");
        }
    }
}
