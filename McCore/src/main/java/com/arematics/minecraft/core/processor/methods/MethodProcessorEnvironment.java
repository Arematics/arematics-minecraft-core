package com.arematics.minecraft.core.processor.methods;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MethodProcessorEnvironment {

    public static MethodProcessorEnvironment newEnvironment(Object executor, Map<String, Object> dataPack,
                                                            List<AnnotationProcessor<?>> processors) {
        return new MethodProcessorEnvironment(executor, dataPack, processors);
    }

    private final Object executor;
    private final Map<String, Object> dataPack;
    private final List<AnnotationProcessor<?>> processors;

    private MethodProcessorEnvironment(Object executor, Map<String, Object> dataPack,
                                       List<AnnotationProcessor<?>> processors){
        this.executor = executor;
        this.dataPack = dataPack;
        this.processors = processors;
    }

    protected Object findData(String key){
        return this.dataPack.get(key);
    }

    public Object getExecutor() {
        return executor;
    }

    public boolean supply(Method method) throws Exception {
        for(AnnotationProcessor<?> processor : this.processors){
            boolean isPresent = !processor.annotationNeeded() || method.isAnnotationPresent(processor.get());
            if(isPresent && !processor.setEnvironment(this).supply(method)) return false;
        }
        return true;
    }
}
