package com.arematics.minecraft.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Methods {

    public static <T extends Annotation> List<T> findAllAnnotation(Object watchingClass, Class<T> theClass){
        return Arrays.stream(watchingClass.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(theClass))
                .map(method -> method.getAnnotation(theClass))
                .collect(Collectors.toList());
    }

    public static <T extends Annotation, R> List<R> fetchAllAnnotationValueSave(Object watchingClass,
                                                                                Class<T> theClass,
                                                                                Function<T, R> execute){
        List<T> all = findAllAnnotation(watchingClass, theClass);
        return all.stream().map(execute).collect(Collectors.toList());
    }

    public static FirstMethodSuccess of(List<Method> methods){
        return new FirstMethodSuccess(methods);
    }
}
