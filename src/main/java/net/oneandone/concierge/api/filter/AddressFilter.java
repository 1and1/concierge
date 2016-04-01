package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AddressFilter implements Filter {

    @Getter private final String address;

}
