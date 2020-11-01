package com.arematics.minecraft.core.processor.methods;

import com.arematics.minecraft.core.generics.UncheckedFunction;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@Data
@RequiredArgsConstructor
public class MethodStack {
    private final List<Method> methods;

    public boolean processEach(UncheckedFunction<Method, Boolean> methodConsumer) throws Exception {
        for(final Method method : getMethods())
            if(methodConsumer.apply(method)) return true;
        return true;
    }
}
