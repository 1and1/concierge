package net.oneandone.concierge.manual;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.oneandone.concierge.api.filter.Filter;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.api.resolver.Resolver;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceManual {

    private Optional<String> description;
    private List<Resolver> availableSubgroups;
    private List<Resolver> availableExtensions;

    private GetManual getManual;

    public JsonStructure content() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if (description.isPresent()) {
            jsonBuilder.add("description", description.get());
        }

        final JsonArrayBuilder operationsBuilder = Json.createArrayBuilder();
        operationsBuilder.add(Json.createObjectBuilder().add("method", "GET").add("manual", getManual.content()).build());
        jsonBuilder.add("operations", operationsBuilder.build());

        final JsonArrayBuilder subgroupsBuilder = Json.createArrayBuilder();
        boolean subgroupAdded = false;
        for (final Resolver availableSubgroup : availableSubgroups) {
            final JsonObjectBuilder subgroupBuilder = Json.createObjectBuilder();
            subgroupBuilder.add("name", availableSubgroup.name());
            if (availableSubgroup.description().isPresent()) {
                subgroupBuilder.add("description", availableSubgroup.description().get());
            }
            subgroupsBuilder.add(subgroupBuilder.build());
            subgroupAdded = true;
        }
        if (subgroupAdded) {
            jsonBuilder.add("available-groups", subgroupsBuilder.build());
        }


        final JsonArrayBuilder extensionsBuilder = Json.createArrayBuilder();
        boolean extensionAdded = false;
        for (final Resolver availableExtension : availableExtensions) {
            final JsonObjectBuilder extensionBuilder = Json.createObjectBuilder();
            extensionBuilder.add("name", availableExtension.name());
            if (availableExtension.description().isPresent()) {
                extensionBuilder.add("description", availableExtension.description().get());
            }
            extensionsBuilder.add(extensionBuilder.build());
            extensionAdded = true;
        }
        if (extensionAdded) {
            jsonBuilder.add("available-extensions", extensionsBuilder.build());
        }

        return jsonBuilder.build();
    }

    public static ResourceManual manual(final GroupResolver resolver, final List<Resolver> availableSubgroups, final List<Resolver> availableExtensions) {
        return new ResourceManual(resolver.description(), availableSubgroups, availableExtensions, new GetManual(resolveParameters(resolver.supportedFilterClasses())));
    }

    public static List<QueryParameter> resolveParameters(final List<Class<? extends Filter>> filterClasses) {
        final List<QueryParameter> parameters = new ArrayList<>();

        for (final Class<? extends Filter> filterClass : filterClasses) {
            for (final Field field : filterClass.getDeclaredFields()) {
                final QueryParameter declaredAnnotation = field.getDeclaredAnnotation(QueryParameter.class);
                if (declaredAnnotation != null) {
                    parameters.add(declaredAnnotation);
                }
            }
        }

        return parameters;
    }
}
