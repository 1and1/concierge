package net.oneandone.concierge.demo.model;

import net.oneandone.concierge.api.Extension;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString @EqualsAndHashCode
public class ProfileExtension implements Extension {

    private final int age;
    private final Gender gender;

    @Override
    public JsonObject content() {
        return Json.createObjectBuilder().add("age", age).add("gender", gender.name().toLowerCase()).build();
    }

    @Override
    public String address() {
        return "profile";
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static ProfileExtension create(final int age, final Gender gender) {
        return new ProfileExtension(age, gender);
    }

    public enum Gender {
        MALE,
        FEMALE
    }
}
