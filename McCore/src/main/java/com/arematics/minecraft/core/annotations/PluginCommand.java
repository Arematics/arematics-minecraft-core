package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Says that the annotated class should be handled as an command
 * Class must extends {@link com.arematics.minecraft.core.command.CoreCommand} for this
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginCommand {
    String[] names();
    Class<? extends AnnotationProcessor<?>>[] processors() default {};
}
