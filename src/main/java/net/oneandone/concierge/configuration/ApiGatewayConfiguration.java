package net.oneandone.concierge.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

@Slf4j
public class ApiGatewayConfiguration {

    @JsonIgnore
    private static ApiGatewayConfiguration apiGatewayConfiguration;

    @JsonProperty(value = "resolvers", required = true)
    @Getter private Resolvers resolvers;

    @JsonProperty(value = "port", required = true)
    @Getter
    private int port;

    //private Constructor
    private ApiGatewayConfiguration() {
    }

    public static ApiGatewayConfiguration getConfiguration(final URL configFile) throws IOException {
        if (apiGatewayConfiguration == null) {
            ObjectMapper mapper = new ObjectMapper();
            apiGatewayConfiguration = mapper.readValue(configFile, ApiGatewayConfiguration.class);
        }
        return apiGatewayConfiguration;
    }

}
