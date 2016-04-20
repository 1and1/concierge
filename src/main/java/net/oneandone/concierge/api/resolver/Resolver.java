package net.oneandone.concierge.api.resolver;

import java.util.Optional;

/** A resolver for a {@link net.oneandone.concierge.api.Group} or {@link net.oneandone.concierge.api.Extension}. */
public interface Resolver {

    /**
     * Returns the name of the resolved group or extension.
     *
     * @return the name of the resolved group or extension
     */
    default String name() {
        if (hierarchy() == null || hierarchy().length <= 0) {
            throw new IllegalStateException("hierarchy for resolver '" + this.getClass().getCanonicalName() + "' must not be null or empty");
        }
        return hierarchy()[hierarchy().length - 1];
    }

    default Optional<String> description() {
        return Optional.empty();
    }

    /**
     * Returns the full hierarchy of types for the resolved group or extension.
     * <p />
     * <b>Example:</b>
     * For the URI {@code group1/id1/group2/id2/extension} the hierarchy must be {@code ("group1", "group2", "extension")}.
     * <p />
     * The returned hierarchy must not be {@code null} or empty.
     *
     * @return the full hierarchy
     */
    String[] hierarchy();
}
