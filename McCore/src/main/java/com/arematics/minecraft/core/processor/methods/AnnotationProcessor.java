package com.arematics.minecraft.core.processor.methods;

import com.arematics.minecraft.core.annotations.ProcessorData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public abstract class AnnotationProcessor<T extends Annotation> implements AnnotationProcessorEnvironment, AnnotationProcessorSupplier {

    private MethodProcessorEnvironment environment;

    @Override
    public AnnotationProcessor<T> setEnvironment(MethodProcessorEnvironment environment){
        this.environment = environment;
        return this;
    }

    @Override
    public boolean supply(Object executor, Method method) throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(ProcessorData.class)){
                String name = getSerializedName(field);
                Object data = environment.getData(getSerializedName(field));
                if(data == null)
                    throw new IllegalStateException("Missing field value in MethodProcessorEnvironment for data " + name);
                if(!(field.getType().isAssignableFrom(data.getClass())))
                    throw new IllegalStateException("Not same Data Types for Field " + field.getName());
                field.setAccessible(true);
                field.set(this, data);
            }
        }
        return true;
    }

    private String getSerializedName(Field field) {
        String name = field.getAnnotation(ProcessorData.class).name();
        if(!name.equals("")) return name;
        return field.getName();
    }

    public Class<T> get(){
        return ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
