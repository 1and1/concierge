package net.oneandone.concierge.example.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import net.oneandone.concierge.api.Extension;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@AllArgsConstructor @EqualsAndHashCode
public class BookRating implements Extension {

    private String goodReads;

    @Override
    public JsonObject content() {
        return Json.createObjectBuilder().add("goodreads.com", goodReads).build();
    }

    @Override
    public String address() {
        return "rating";
    }

    @Override
    public ZonedDateTime lastModified() {
        return ZonedDateTime.of(LocalDateTime.ofEpochSecond(this.hashCode(), 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }
}
