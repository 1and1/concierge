package net.oneandone.concierge.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import net.oneandone.concierge.api.Element;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor
@EqualsAndHashCode
public class Author implements Element {

    private long id;
    private String name;
    private String nationality;

    @Override
    public String group() {
        return "books";
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
        return name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s", ".");
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }
}
