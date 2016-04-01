package net.oneandone.concierge.demo.model;

import net.oneandone.concierge.api.Element;
import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString @EqualsAndHashCode
public class Post implements Element {

    @Getter private final long id;
    @Getter private final String title;
    @Getter private final String text;

    @Override
    public String group() {
        return "posts";
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public JsonObject content() {
        return Json.createObjectBuilder().add("title", title).add("text", text).build();
    }

    @Override
    public String address() {
        return title.toLowerCase().replaceAll("\\s", "-").replaceAll("[^a-z-]", "");
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static Post create(final long id, final String title, final String text) {
        return new Post(id, title, text);
    }
}
