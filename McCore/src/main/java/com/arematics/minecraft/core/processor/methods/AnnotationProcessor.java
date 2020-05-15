package com.arematics.minecraft.core.processor.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AnnotationProcessor<T extends Annotation> {

    public abstract Object[] process(Method method);
}
