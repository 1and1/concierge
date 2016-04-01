package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
public class PageFilter implements Filter {

    @Getter private final int page;
    @Getter private final Optional<Integer> perPage;

}
