package net.oneandone.concierge.api;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/** An elements group. */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    /* ********** EMPTY GROUP **********************************************************/
    public static Group empty(final String name) {
        return new EmptyGroup(name);
    }

    static class EmptyGroup extends Group {
        EmptyGroup(final String address) {
            super(address, Collections.emptyList(), 0, ZonedDateTime.now());
        }
    }

    /* ********** SINGLE ELEMENT GROUP *************************************************/
    public static Group withElement(final Element element) {
        Preconditions.checkNotNull(element, "the element may not be null");
        return new SingleElementGroup(element);
    }

    static class SingleElementGroup extends Group {
        SingleElementGroup(final Element element) {
            super(element.group(), Collections.singletonList(element), 1, element.lastModified());
        }
    }

    /* ********** MULTI ELEMENT GROUP **************************************************/
    public static Group withElements(final String name, final List<Element> elements, final int total, final ZonedDateTime zonedDateTime) {
        Preconditions.checkNotNull(elements, "the elements may not be null");
        return new MultiElementGroup(name, elements, total, zonedDateTime);
    }

    static class MultiElementGroup extends Group {
        MultiElementGroup(String name, List<Element> elements, int total, ZonedDateTime lastModified) {
            super(name, elements, total, lastModified);
        }
    }
}
