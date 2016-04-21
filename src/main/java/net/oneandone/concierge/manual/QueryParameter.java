package net.oneandone.concierge.manual;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParameter {

    String id();
    String description();

}
