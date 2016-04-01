package net.oneandone.concierge.demo.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.demo.model.DemoData;
import net.oneandone.concierge.demo.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class PostResolver implements GroupResolver {

    @Override
    public Optional<String> parentGroup() {
        return Optional.of("users");
    }

    @Override
    public int defaultPageSize() {
        return 0;
    }

    @Override
    public int maximumPageSize() {
        return 0;
    }

    @Override
    public Group elements(final Element parent, final Filters filters) {
        final Optional<User> user = DemoData.USER_POSTS.keySet().stream().filter(u -> u.address().equals(parent.address())).findAny();
        if (user.isPresent()) {
            final List<Element> posts = new ArrayList<>(DemoData.USER_POSTS.get(user.get()));

            final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
            if (addressFilter.isPresent()) {
                final Optional<Element> post = posts.stream().filter(p -> p.address().equals(addressFilter.get().getAddress())).findFirst();
                if (post.isPresent()) {
                    return new Group(name(), Collections.singletonList(user.get()), 1, post.get().lastModified());
                }
                return Group.empty(name());
            }

            return new Group(name(), posts, posts.size(), ZonedDateTime.of(LocalDateTime.ofEpochSecond(posts.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC));
        }
        return Group.empty(name());
    }

    @Override
    public String name() {
        return "posts";
    }

}
