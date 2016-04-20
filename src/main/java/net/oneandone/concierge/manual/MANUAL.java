package net.oneandone.concierge.manual;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("MANUAL")
@Documented
public @interface MANUAL {
}
