package com.arematics.minecraft.core.processor.methods;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AnnotationProcessor {

    private Map<String, Object> data = new HashMap<>();

    public AnnotationProcessor addDataPack(Map<String, Object> dataPack){
        this.data.putAll(dataPack);
        return this;
    }

    public AnnotationProcessor addData(CommonData key, Object object){
        return addData(key.getKey(), object);
    }

    public AnnotationProcessor addData(String key, Object object){
        this.data.put(key, object);
        return this;
    }

    protected Object getData(CommonData key){
        return getData(key.toString());
    }

    protected Object getData(String key){
        return this.data.get(key);
    }

    public abstract boolean supply(Object executing, Method method) throws Exception;
}
