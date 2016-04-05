package net.oneandone.concierge;

import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public interface JsonHelper {

    public static String getJsonStringFor(final Class testClass, final String test) throws IOException {
        final String jsonPath = "/json/" + testClass.getSimpleName() + "/" + test + ".json";
        LoggerFactory.getLogger(JsonHelper.class).info("requesting json resource '{}'", jsonPath);

        final URL jsonUrl = JsonHelper.class.getResource(jsonPath);
        if (jsonUrl == null) {
            throw new NullPointerException("resource '" + jsonPath + "' not found");
        }

        try (final InputStream stream = jsonUrl.openStream();
             final Reader reader = new InputStreamReader(stream, "utf-8")) {
            final JsonReader jsonReader = Json.createReader(reader);
            return jsonReader.read().toString();
        }
    }

}
