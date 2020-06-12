package com.arematics.minecraft.core.command.annotations;

import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Processors {
    Class<? extends AnnotationProcessor<?>>[] processors();
}
