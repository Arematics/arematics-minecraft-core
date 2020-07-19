package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.data.TimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataCacheService {

    TimeType timeType() default TimeType.MINUTE;
    int value() default 5;
    boolean async() default false;
}
