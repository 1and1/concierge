package net.oneandone.concierge;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public interface JsonHelper {

    public static String getJsonStringFor(final Class testClass, final String test) throws IOException {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream("json/" + testClass.getSimpleName() + "/" + test + ".json");
             final Reader reader = new InputStreamReader(stream)) {
            final JsonReader jsonReader = Json.createReader(reader);
            return jsonReader.read().toString();
        }
    }

}
