package net.oneandone.concierge.api.resolver;

public interface Resolver {

    default String name() {
        if (hierarchy() == null || hierarchy().length <= 0) {
            throw new IllegalStateException("hierarchy '" + hierarchy() + "' for resolver '" + this.getClass().getCanonicalName() + "' must not be null or empty");
        }
        return hierarchy()[hierarchy().length - 1];
    }

    String[] hierarchy();
}
