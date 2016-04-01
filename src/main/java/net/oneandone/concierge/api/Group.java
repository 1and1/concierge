package net.oneandone.concierge.api;

import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/** An elements group. */
@AllArgsConstructor
public class Group implements Addressable {

    private final String address;
    private final List<Element> elements;
    private final int total;
    private final ZonedDateTime lastModified;

    /**
     * Returns the list of elements that belong to this group.
     *
     * @return the list of elements
     */
    public List<Element> elements() {
        return elements;
    }

    /**
     * Returns the total number of elements for this group.
     * <p />
     * An instance of this class may contain only a subset of all available elements.
     *
     * @return the total number of elements for this group
     */
    public int total() {
        return total;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public ZonedDateTime lastModified() {
        return lastModified;
    }

}
