package com.arematics.minecraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for {@link com.arematics.minecraft.core.command.CoreCommand}, saying one of all access checks from this command
 * is enough that the executor could use the command
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnyAccess {
}
