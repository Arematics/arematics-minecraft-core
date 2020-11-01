package com.arematics.minecraft.core.generics;

@FunctionalInterface
public interface UncheckedConsumer<T> {
    public void accept(T t) throws Exception;
}
