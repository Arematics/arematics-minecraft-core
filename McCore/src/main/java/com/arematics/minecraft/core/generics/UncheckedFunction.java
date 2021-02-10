package com.arematics.minecraft.core.generics;

@FunctionalInterface
public interface UncheckedFunction<T, R> {
    R apply(T t) throws Throwable;
}
