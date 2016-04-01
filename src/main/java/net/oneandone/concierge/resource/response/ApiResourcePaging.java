package net.oneandone.concierge.resource.response;

import lombok.Builder;

@Builder
public class ApiResourcePaging {

    private final String group;
    private final int page;
    private final int perPage;
    private final int total;

    public String getAcceptRanges() {
        return group;
    }

    public String getContentRange() {
        int min = (page * perPage) - perPage;
        int max = Math.min((page * perPage), total) - 1;
        return getAcceptRanges() + " " + min + "-" + max + "/" + total;
    }

}
