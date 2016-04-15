package net.oneandone.concierge.api;

import java.time.ZonedDateTime;

/** Addressable resource (in REST context). */
public interface Addressable {

    /**
     * Returns the resource name or type.
     *
     * @return the resource name or type
     */
    String address();

    /**
     * Returns the date time with timezone of the last update.
     *
     * @return the date time with timezone of the last update
     */
    ZonedDateTime lastModified();

}
