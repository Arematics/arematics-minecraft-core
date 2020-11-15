package com.arematics.minecraft.core.annotations;

import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Validator {
    Class<? extends ParameterValidator<?>>[] validators();
}
