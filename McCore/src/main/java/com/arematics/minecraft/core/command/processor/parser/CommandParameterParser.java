package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandParameterParser<T> {

    public final T processParse(String value, Parameter parameter, List<Object> data) throws CommandProcessException {
        T parsed = parse(value);
        postParse(parsed, parameter, data);
        return parsed;
    }

    public final void postParse(T result, Parameter parameter, List<Object> data) throws CommandProcessException {
        if(parameter.isAnnotationPresent(Validator.class)){
            List<ParameterValidator<?>> validators = Arrays.stream(parameter.getAnnotation(Validator.class).validators())
                    .map(parameterValidatorClass -> Boots.getBoot(CoreBoot.class).getContext().getBean(parameterValidatorClass))
                    .collect(Collectors.toList());
            for(ParameterValidator<?> validator : validators){
                Class<?> classType = validator.getType();
                if(matchingType(classType))
                    ((ParameterValidator<T>) validator).supply(correctResult(classType, result), data);
            }
        }
    }

    public boolean matchingType(Class<?> classType){
        return classType.equals(getType()) || (classType == User.class && getType() == CorePlayer.class);
    }

    public T correctResult(Class<?> classType, T result){
        return (classType == User.class && getType() == CorePlayer.class) ? (T) ((CorePlayer) result).getUser() : result;
    }

    public abstract T parse(String value) throws CommandProcessException;

    Class<T> getType(){
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
