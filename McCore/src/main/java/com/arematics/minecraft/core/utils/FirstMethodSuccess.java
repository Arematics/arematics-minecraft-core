package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.generics.UncheckedFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FirstMethodSuccess {
    private final List<Method> methods;

    public boolean apply(UncheckedFunction<Method, Boolean> methodConsumer) throws Throwable {
        for(final Method method : getMethods())
            if(methodConsumer.apply(method)) return true;
        return false;
    }
}
