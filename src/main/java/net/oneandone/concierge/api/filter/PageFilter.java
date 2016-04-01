package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.OptionalInt;

@AllArgsConstructor
public class PageFilter implements Filter {

    @Getter private final int page;
    @Getter private final OptionalInt perPage;

}
