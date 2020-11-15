package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.ParserException;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class ParameterValidator<T> {
    public abstract void supply(T result, List<Object> data) throws ParserException;

    public Class<T> getType(){
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
