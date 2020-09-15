package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disable Hooking for this class. Used for register listeners by themself or disable command registration
 * Could also be used to whitelist or blacklist specific hooks.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableAutoHook {
    Class<? extends Hook>[] list() default {};
    boolean whitelist() default true;
}
