package com.arematics.minecraft.core.processor.methods;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodProcessorEnvironment {

    private final Object executor;
    private final Method method;
    private final Map<String, Object> data;

    public MethodProcessorEnvironment(Object executor, Method method){
        this(executor, method, new HashMap<>());
    }

    public MethodProcessorEnvironment(Object executor, Method method, Map<String, Object> data){
        this.executor = executor;
        this.method = method;
        this.data = data;
    }

    public MethodProcessorEnvironment addDataPack(Map<String, Object> dataPack){
        this.data.putAll(dataPack);
        return this;
    }

    public MethodProcessorEnvironment addData(CommonData key, Object object){
        return addData(key.getKey(), object);
    }

    public MethodProcessorEnvironment addData(String key, Object object){
        this.data.put(key, object);
        return this;
    }

    protected Object getData(CommonData key){
        return getData(key.toString());
    }

    protected Object getData(String key){
        return this.data.get(key);
    }
}
