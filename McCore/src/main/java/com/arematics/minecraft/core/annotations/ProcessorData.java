package com.arematics.minecraft.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in {@link com.arematics.minecraft.core.processor.methods.AnnotationProcessor} for inject declared fields
 * Data Values
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProcessorData {
    String name() default "";
}
