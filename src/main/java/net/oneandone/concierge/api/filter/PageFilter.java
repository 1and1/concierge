package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PageFilter implements Filter {

    @Getter private final int page;
    @Getter private final int perPage;

}
