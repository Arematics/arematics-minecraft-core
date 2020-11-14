package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandParameterParser<T> {

    public final T processParse(String value, Parameter parameter, List<Object> data) throws ParserException{
        T parsed = parse(value);
        postParse(parsed, parameter, data);
        return parsed;
    }

    public final void postParse(T result, Parameter parameter, List<Object> data) throws ParserException{
        if(parameter.isAnnotationPresent(Validator.class)){
            List<ParameterValidator<?>> validators = Arrays.stream(parameter.getAnnotation(Validator.class).validators())
                    .map(parameterValidatorClass -> Boots.getBoot(CoreBoot.class).getContext().getBean(parameterValidatorClass))
                    .collect(Collectors.toList());
            for(ParameterValidator<?> validator : validators){
                Class<?> classType = validator.getType();
                System.out.println(classType);
                if(classType.equals(getType()))
                    ((ParameterValidator<T>) validator).supply(result, data);
            }
        }
    }

    public abstract T parse(String value) throws ParserException;

    Class<T> getType(){
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
