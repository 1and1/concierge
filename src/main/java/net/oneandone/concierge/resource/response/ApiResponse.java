package net.oneandone.concierge.resource.response;

import lombok.Getter;

import javax.json.JsonStructure;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ApiResponse {

    @Getter private JsonStructure object;
    @Getter private ZonedDateTime lastModified;
    @Getter private Optional<ApiResourcePaging> paging;

    private ApiResponse(final JsonStructure object, final ZonedDateTime lastModified, final Optional<ApiResourcePaging> paging) {
        this.object = object;
        this.lastModified = lastModified;
        this.paging = paging;
    }

    public static ApiResponse create(final JsonStructure object, final ZonedDateTime lastModified) {
        return new ApiResponse(object, lastModified, Optional.empty());
    }

    public static ApiResponse create(final JsonStructure object, final ZonedDateTime lastModified, final ApiResourcePaging paging) {
        return new ApiResponse(object, lastModified, Optional.of(paging));
    }

}
