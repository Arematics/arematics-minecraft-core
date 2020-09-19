package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.ProcessorData;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public class PermissionAnnotationProcessor extends AnnotationProcessor<Perm> {

    @ProcessorData
    private String classLevelPermission;
    @ProcessorData
    private CommandSender sender;

    @Override
    public boolean supply(Method method) throws Exception {
        super.supply(method);
        String result = getSerializedPermission(method);
        if(StringUtils.isBlank(classLevelPermission)){
            Messages.create("cmd_upper_permission_not_set")
                    .FAILURE()
                    .to(sender)
                    .handle();
            return false;
        }
        result = this.classLevelPermission + "." + result;
        if(Permissions.isNotAllowed(this.sender, result)){
            Messages.create("cmd_noperms")
                    .WARNING()
                    .to(sender)
                    .handle();
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
