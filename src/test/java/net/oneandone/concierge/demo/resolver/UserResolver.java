package net.oneandone.concierge.demo.resolver;

import net.oneandone.concierge.api.Element;
import net.oneandone.concierge.api.Group;
import net.oneandone.concierge.api.filter.AddressFilter;
import net.oneandone.concierge.api.filter.Filters;
import net.oneandone.concierge.api.filter.PageFilter;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.demo.model.DemoData;
import net.oneandone.concierge.demo.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserResolver implements GroupResolver {

    @Override
    public Optional<String> parentGroup() {
        return Optional.empty();
    }

    @Override
    public int defaultPageSize() {
        return 3;
    }

    @Override
    public int maximumPageSize() {
        return 10;
    }

    @Override
    public Group elements(final Element parent, final Filters filters) {
        final Optional<AddressFilter> addressFilter = filters.get(AddressFilter.class);
        if (addressFilter.isPresent()) {
            final Optional<User> user = DemoData.USER_POSTS.keySet().stream().filter(u -> u.address().equals(addressFilter.get().getAddress())).findFirst();
            if (user.isPresent()) {
                return new Group(name(), Collections.singletonList(user.get()), 1, user.get().lastModified());
            }
            return null;
        }

        final Optional<PageFilter> pageFilter = filters.get(PageFilter.class);

        final int page;
        final int perPage;
        if (pageFilter.isPresent()) {
            page = pageFilter.get().getPage();
            perPage = pageFilter.get().getPerPage();
        } else {
            page = 1;
            perPage = defaultPageSize();
        }

        final int skip = (page - 1) * perPage;
        final int limit = perPage;
        final List<Element> elements = DemoData.USER_POSTS.keySet().stream().skip(skip).limit(limit).collect(Collectors.toList());

        return new Group(name(), elements, DemoData.USER_POSTS.keySet().size(), ZonedDateTime.of(LocalDateTime.ofEpochSecond(elements.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC));
    }

    @Override
    public String name() {
        return "users";
    }
}
