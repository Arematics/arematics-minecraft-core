package com.arematics.minecraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for protect Commands with Permissions.
 *
 * Methods would ne used as short string if Class has annotated permission. Example Case:
 * Class is annotated an has permission = "admin" and SubCommand Method is annotated and has permission = "test".
 * Also an other SubCommand Method is annotated and has permission = "test2".
 * Needed permission in absolute is admin.test.
 *
 * If user has permission admin he is allowed to perform both Methods. If he has only permission admin.test he
 * couldn't execute method admin.test2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Permission {
    String permission();
    String description() default "";
}
