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
public class User implements Element {

    @Getter private final long id;
    @Getter private final String name;

    @Override
    public String group() {
        return "users";
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public JsonObject content() {
        return Json.createObjectBuilder().add("name", name).build();
    }

    @Override
    public String address() {
        return name.toLowerCase().replaceAll("\\s", ".");
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static User create(final long id, final String name) {
        return new User(id, name);
    }
}
