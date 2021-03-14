package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.ProcessorData;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class PermissionAnnotationProcessor extends AnnotationProcessor<Perm> {

    @ProcessorData
    private String classLevelPermission;
    @ProcessorData
    private CorePlayer sender;

    @Override
    public boolean supply(Method method) throws Exception {
        super.supply(method);
        if(StringUtils.isBlank(classLevelPermission))
            return true;
        String methodName = getSerializedPermission(method);
        String result = this.classLevelPermission;
        if(StringUtils.isNotBlank(methodName)) result += "." + methodName;
        if(!sender.hasPermission(result)){
            sender.warn("cmd_noperms").handle();
            return false;
        }
        return true;
    }

    @Override
    public boolean annotationNeeded() {
        return false;
    }

    private String getSerializedPermission(Method method) {
        if(method.isAnnotationPresent(Perm.class)) return method.getAnnotation(Perm.class).permission();
        return "";
    }
}
