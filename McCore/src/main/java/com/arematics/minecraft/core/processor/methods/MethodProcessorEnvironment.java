package com.arematics.minecraft.core.processor.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class MethodProcessorEnvironment {

    public static MethodProcessorEnvironment newEnvironment(Object executor, Map<String, Object> dataPack,
                                                            Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors) {
        return new MethodProcessorEnvironment(executor, dataPack, processors);
    }

    private final Object executor;
    private final Map<String, Object> dataPack;
    private final Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors;

    private MethodProcessorEnvironment(Object executor, Map<String, Object> dataPack,
                                       Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors){
        this.executor = executor;
        this.dataPack = dataPack;
        this.processors = processors;
    }

    public MethodProcessorEnvironment addDataPack(Map<String, Object> dataPack){
        this.dataPack.putAll(dataPack);
        return this;
    }

    public MethodProcessorEnvironment addData(String key, Object object){
        this.dataPack.put(key, object);
        return this;
    }

    protected Object findData(String key){
        return this.dataPack.get(key);
    }

    public Object getExecutor() {
        return executor;
    }

    public boolean supply(Method method) throws Exception {
        for(Map.Entry<Class<? extends Annotation>, AnnotationProcessor<?>> processorEntry : this.processors.entrySet()){
            if(method.isAnnotationPresent(processorEntry.getKey()) &&
                    !processorEntry.getValue().setEnvironment(this).supply(method)){
                return false;
            }
        }
        return true;
    }
}
