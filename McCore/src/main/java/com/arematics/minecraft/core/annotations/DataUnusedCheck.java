package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.data.TimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DataUnusedCheck is made for generating events that would delete data after a specified amount of time
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataUnusedCheck {

    TimeType timeType();
    int value();
}
