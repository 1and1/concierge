package net.oneandone.concierge.api.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.Group;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Resolves extensions for elements. */
public interface ExtensionResolver extends Resolver {

    /**
     * Resolves and returns the optional extension for the specified parent element.
     *
     * @param element the parent element
     * @return the optional extension for the specified parent element
     */
    public Optional<Extension> resolve(final Element element);

    /**
     * Resolves and returns the extensions for a specified group.
     *
     * @param group the group
     * @return the map of the group elements and their extensions
     */
    public default Map<Element, Extension> resolve(final Group group) {
        final Map<Element, Extension> result = new HashMap<>();
        for (final Element element : group.elements()) {
            final Optional<Extension> resolvedResult = resolve(element);
            if (resolvedResult.isPresent()) {
                result.put(element, resolvedResult.get());
            }
        }
        return result;
    }

}
