package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.OptionalInt;

/** Pages the elements of a {@link net.oneandone.concierge.api.Group}. */
@AllArgsConstructor
public class PageFilter implements Filter {

    /** The page to display. */
    @Getter private final int page;

    /** The optional number of elements per page. */
    @Getter private final OptionalInt perPage;

}
