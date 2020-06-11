package com.arematics.minecraft.core.processor.methods.fluent;

import java.lang.reflect.Method;

public interface AnnotationProcessorSupplier {
    boolean supply(Object executor, Method method) throws Exception;
}
