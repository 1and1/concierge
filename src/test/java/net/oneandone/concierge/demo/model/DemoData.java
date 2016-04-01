package net.oneandone.concierge.demo.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;

public interface DemoData {

    public static final User USER1 = User.create(1L, "Andreas Piranha87");
    public static final User USER2 = User.create(1L, "Tobias Netdevfighter");
    public static final User USER3 = User.create(1L, "Daniel Germandrummer92");
    public static final User USER4 = User.create(1L, "Johann Bitionaire");

    public static final Multimap<User, Post> USER_POSTS = ImmutableMultimap.<User, Post>builder()
            .put(USER1, Post.create(1L, "Creativity", "I like to destroy, not to create!"))
            .put(USER4, Post.create(2L, "Jesus is back!", "Nope! It's just Chuck Testa."))
            .put(USER4, Post.create(3L, "Darkwing Duck", "Let's get dangerous!"))
            .put(USER2, Post.create(4L, "Go hard or go home", "Work smarter not harder ... and then go home!"))
            .put(USER3, Post.create(5L, "My thoughts on leadership", "I'm here to lead not to read!"))
            .build();

    public static final Map<User, ProfileExtension> USER_PROFILES = ImmutableMap.<User, ProfileExtension> builder()
            .put(USER4, ProfileExtension.create(29, ProfileExtension.Gender.MALE))
            .build();

}
