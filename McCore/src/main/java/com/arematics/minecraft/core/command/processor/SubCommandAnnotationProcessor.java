package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.annotations.ProcessorData;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.MethodUtils;

import java.lang.reflect.Method;

public class SubCommandAnnotationProcessor extends AnnotationProcessor<SubCommand> {

    private static final String CMD_SAME_SUB_METHOD = "cmd_same_sub_method_exist";

    @ProcessorData
    private String[] arguments;
    @ProcessorData
    private CorePlayer sender;

    @Override
    public boolean supply(Method method) throws Exception {
        super.supply(method);

        String value = getSerializedValue(method);
        String[] annotationValues = value.split(" ");
        Object[] order;
        order = Parser.getInstance().fillParameters(sender, annotationValues, method.getParameters(), arguments);

        if(ArrayUtils.isEmpty(order)){
            method.invoke(executer(), sender);
            return true;
        }

        MethodUtils.invokeMethod(executer(), method.getName(), order,
                method.getParameterTypes());
        return true;
    }

    @Override
    public boolean annotationNeeded() {
        return true;
    }

    private String getSerializedValue(Method method) {
        return method.getAnnotation(SubCommand.class).value();
    }
}
