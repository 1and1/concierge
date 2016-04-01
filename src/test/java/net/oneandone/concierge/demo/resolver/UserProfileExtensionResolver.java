package net.oneandone.concierge.demo.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Extension;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.demo.model.DemoData;
import net.oneandone.concierge.demo.model.User;

import java.util.Optional;

public class UserProfileExtensionResolver implements ExtensionResolver {
    @Override
    public String forGroup() {
        return "users";
    }

    @Override
    public Optional<Extension> resolve(final Element element) {
        final Optional<User> user = DemoData.USER_POSTS.keySet().stream().filter(u -> u.address().equals(element.address())).findAny();
        if (user.isPresent()) {
            return Optional.ofNullable(DemoData.USER_PROFILES.get(user.get()));
        }
        return Optional.empty();
    }

    @Override
    public String name() {
        return "profile";
    }
}
