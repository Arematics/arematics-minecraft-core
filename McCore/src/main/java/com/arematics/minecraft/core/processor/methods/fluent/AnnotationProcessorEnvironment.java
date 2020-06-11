package com.arematics.minecraft.core.processor.methods.fluent;

import com.arematics.minecraft.core.processor.methods.MethodProcessorEnvironment;

public interface AnnotationProcessorEnvironment {
    AnnotationProcessorSupplier setEnvironment(MethodProcessorEnvironment environment);
}
