package net.oneandone.concierge.api;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/** An elements group. */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("WeakerAccess")
public class Group implements Addressable {

    private final String address;
    private final List<Element> elements;
    private final int total;
    private final ZonedDateTime lastModified;

    /* ********** EMPTY GROUP **********************************************************/

    /**
     * Returns an empty group.
     *
     * @param name the group name
     * @return an empty group
     */
    public static Group empty(final String name) {
        return new EmptyGroup(name);
    }

    /* ********** SINGLE ELEMENT GROUP *************************************************/

    /**
     * Returns a result containing only one unique element, as the result of a query with an {@link net.oneandone.concierge.api.filter.AddressFilter}.
     *
     * @param element the unique element
     * @return a group with a single element
     */
    public static Group withElement(final Element element) {
        Preconditions.checkNotNull(element, "the element may not be null");
        return new SingleElementGroup(element);
    }

    /* ********** MULTI ELEMENT GROUP **************************************************/

    /**
     * Returns a multi element group.
     *
     * @param name          the group name
     * @param elements      the list of elements for this group (might be a paged result)
     * @param total         the number of total results for this group
     * @param zonedDateTime the date of the last update
     * @return multi element group
     */
    public static Group withElements(final String name, final List<Element> elements, final int total, final ZonedDateTime zonedDateTime) {
        Preconditions.checkNotNull(elements, "the elements may not be null");
        return new MultiElementGroup(name, elements, total, zonedDateTime);
    }

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
     * <p/>
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

    static class EmptyGroup extends Group {
        EmptyGroup(final String address) {
            super(address, Collections.emptyList(), 0, ZonedDateTime.now());
        }
    }

    static class SingleElementGroup extends Group {
        SingleElementGroup(final Element element) {
            super(element.group(), Collections.singletonList(element), 1, element.lastModified());
        }
    }

    static class MultiElementGroup extends Group {
        MultiElementGroup(String name, List<Element> elements, int total, ZonedDateTime lastModified) {
            super(name, elements, total, lastModified);
        }
    }
}
