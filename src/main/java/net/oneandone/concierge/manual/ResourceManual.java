package net.oneandone.concierge.manual;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.oneandone.concierge.api.resolver.Resolver;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceManual {

    private Optional<String> description;

    private GetManual getManual;

    private JsonStructure content() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if (description.isPresent()) {
            jsonBuilder.add("description", description.get());
        }

        final JsonArrayBuilder operationsBuilder = Json.createArrayBuilder();
        operationsBuilder.add(Json.createObjectBuilder().add("method", "GET").add("manual", getManual.content()).build());
        jsonBuilder.add("operations", operationsBuilder.build());

        return jsonBuilder.build();
    }

    public static ResourceManual manual(final Resolver resolver) {


        return null;
    }
}
