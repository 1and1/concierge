package net.oneandone.concierge.api;

import javax.json.JsonObject;

/** Represents an extension of an {@link Element}. */
public interface Extension extends Addressable {

    /**
     * Returns the JSON representation of this element.
     *
     * @return the JSON representation
     */
    public JsonObject content();
}
