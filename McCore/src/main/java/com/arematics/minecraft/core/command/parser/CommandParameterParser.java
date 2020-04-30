package com.arematics.minecraft.core.command.parser;

import java.lang.reflect.ParameterizedType;

public abstract class CommandParameterParser<T> {

    public abstract T doParse(String value) throws ParserException;

    Class<T> getType(){
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
