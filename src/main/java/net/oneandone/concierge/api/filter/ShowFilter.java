package net.oneandone.concierge.api.filter;

import net.oneandone.concierge.manual.QueryParameter;

public class ShowFilter implements Filter {

    @QueryParameter(id = "show", description = "The extensions to show.")
    private String[] extensions;

}
