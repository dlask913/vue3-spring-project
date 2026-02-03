package com.limnj.noticeboardadmin.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    String eventType() default "";
    String actionType() default "";
}