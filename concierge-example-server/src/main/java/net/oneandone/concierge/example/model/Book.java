package net.oneandone.concierge.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import net.oneandone.concierge.api.Element;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor @EqualsAndHashCode
public class Book implements Element {

    private long id;
    private String title;

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
        return Json.createObjectBuilder().add("title", title).build();
    }

    @Override
    public String address() {
        return title.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s", ".");
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }
}
