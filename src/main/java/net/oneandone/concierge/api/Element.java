package net.oneandone.concierge.api;

import javax.json.JsonObject;

/** Represents a uniquely identifiable element within a {@link Group}. */
public interface Element extends Addressable {

    /**
     * Returns the group this element belongs to.
     *
     * @return the group
     */
    public String group();

    /**
     * The unique id of this element.
     *
     * @return the unique id
     */
    public Long id();

    /**
     * Returns the JSON representation of this element.
     *
     * @return the JSON representation
     */
    public JsonObject content();
}
