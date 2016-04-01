package net.oneandone.concierge.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiGatewayConfiguration extends Configuration {

    @JsonProperty(value = "resolvers", required = true)
    @Getter private Resolvers resolvers;

}
