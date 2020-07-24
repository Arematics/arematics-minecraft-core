package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.data.TimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines Caching Service Data for Data Model Class. Could be made async with async = true
 * Default Values are: TimeType = TimeType.MINUTE, Value = 5 and async = false. Saying every 5 Minutes the data would
 * be saved to the database sync.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataCacheService {

    TimeType timeType() default TimeType.MINUTE;
    int value() default 5;
    boolean async() default false;
}
