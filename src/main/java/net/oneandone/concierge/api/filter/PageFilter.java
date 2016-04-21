package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.oneandone.concierge.manual.QueryParameter;

import java.util.OptionalInt;

/** Pages the elements of a {@link net.oneandone.concierge.api.Group}. */
@AllArgsConstructor
public class PageFilter implements Filter {

    /** The page to display. */
    @QueryParameter(id = "page", description = "The page to display, starting from page '1'. The page range is specified in the 'Content-Range' header.")
    @Getter private final int page;

    /** The optional number of elements per page. */
    @QueryParameter(id = "per_page", description = "The page element count.")
    @Getter private final OptionalInt perPage;

}
