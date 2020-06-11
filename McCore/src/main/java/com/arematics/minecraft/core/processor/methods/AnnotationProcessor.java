package com.arematics.minecraft.core.processor.methods;

import com.arematics.minecraft.core.command.annotations.SubCommand;
import com.arematics.minecraft.core.processor.methods.fluent.AnnotationProcessorEnvironment;
import com.arematics.minecraft.core.processor.methods.fluent.AnnotationProcessorSupplier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

public abstract class AnnotationProcessor implements AnnotationProcessorEnvironment, AnnotationProcessorSupplier {

    private MethodProcessorEnvironment environment;

    @Override
    public AnnotationProcessor setEnvironment(MethodProcessorEnvironment environment){
        this.environment = environment;
        return this;
    }

    @Override
    public boolean supply(Object executor, Method method) throws Exception {
        Field[] fields = this.getClass().getFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(Data.class)){
                Object data = environment.getData(getSerializedName(field));
                if(data == null)
                    throw new IllegalStateException("Missing field value in MethodProcessorEnvironment");
                if(!(data.getClass() == field.getType()))
                    throw new IllegalStateException("Not same Data Types for Field " + field.getName());
                field.setAccessible(true);
                field.set(this, data);
            }
        }
        return true;
    }

    private String getSerializedName(Field field) {
        String name = field.getAnnotation(Data.class).name();
        if(!name.equals("")) return name;
        return field.getName();
    }
}
