package com.arematics.minecraft.core.processor.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodProcessorEnvironment {

    private final Object executor;
    private final Method method;
    private final Map<String, Object> dataPack;

    public MethodProcessorEnvironment(Object executor, Method method){
        this(executor, method, new HashMap<>());
    }

    public MethodProcessorEnvironment(Object executor, Method method, Map<String, Object> dataPack){
        this.executor = executor;
        this.method = method;
        this.dataPack = dataPack;
    }

    public MethodProcessorEnvironment addDataPack(Map<String, Object> dataPack){
        this.dataPack.putAll(dataPack);
        return this;
    }

    public MethodProcessorEnvironment addData(CommonData key, Object object){
        return addData(key.getKey(), object);
    }

    public MethodProcessorEnvironment addData(String key, Object object){
        this.dataPack.put(key, object);
        return this;
    }

    protected Object findData(CommonData key){
        return findData(key.toString());
    }

    protected Object findData(String key){
        return this.dataPack.get(key);
    }

    public Object getExecutor() {
        return executor;
    }

    public Method getMethod() {
        return method;
    }

    public boolean supplyProcessors(Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors) throws Exception {
        for(Map.Entry<Class<? extends Annotation>, AnnotationProcessor<?>> processorEntry : processors.entrySet()){
            if(method.isAnnotationPresent(processorEntry.getKey())){
                if (processorEntry.getValue().setEnvironment(this).supply()) return true;
            }
        }
        return true;
    }
}
