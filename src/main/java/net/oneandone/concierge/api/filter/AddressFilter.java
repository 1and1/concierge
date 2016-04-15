package net.oneandone.concierge.api.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.oneandone.concierge.api.Addressable;

/**
 * An address filter.
 *
 * @see Addressable#address()
 */
@AllArgsConstructor
public class AddressFilter implements Filter {

    /** The address to accept. */
    @Getter private final String address;

}
