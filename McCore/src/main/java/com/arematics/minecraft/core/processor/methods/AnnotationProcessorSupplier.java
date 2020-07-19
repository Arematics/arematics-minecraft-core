package com.arematics.minecraft.core.processor.methods;

import java.lang.reflect.Method;

public interface AnnotationProcessorSupplier {
    boolean supply(Method method) throws Exception;
}
