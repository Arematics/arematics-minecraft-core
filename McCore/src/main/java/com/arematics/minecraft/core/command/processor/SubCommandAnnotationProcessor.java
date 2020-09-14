package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.annotations.ProcessorData;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public class SubCommandAnnotationProcessor extends AnnotationProcessor<SubCommand> {

    private static final String CMD_SAME_SUB_METHOD = "cmd_same_sub_method_exist";

    @ProcessorData
    private String[] arguments;
    @ProcessorData
    private CommandSender sender;

    @Override
    public boolean supply(Method method) throws Exception {
        super.supply(method);

        String value = getSerializedValue(method);
        String[] annotationValues = value.split(" ");
        Object[] order;
        try{
            order = Parser.getInstance().fillParameters(sender, annotationValues, method.getParameterTypes(), arguments);
        }catch (ParserException exception){
            Messages.create(exception.getMessage())
                    .WARNING()
                    .to(sender)
                    .handle();
            return true;
        }

        if(ArrayUtils.isEmpty(order)) return (boolean) method.invoke(executer(), sender);
        else return (boolean) MethodUtils.invokeMethod(executer(), method.getName(), order,
                method.getParameterTypes());
    }

    @Override
    public boolean annotationNeeded() {
        return true;
    }

    private String getSerializedValue(Method method) {
        return method.getAnnotation(SubCommand.class).value();
    }
}
