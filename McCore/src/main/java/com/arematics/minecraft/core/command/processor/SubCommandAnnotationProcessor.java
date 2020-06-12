package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.command.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.CommonData;
import com.arematics.minecraft.core.processor.methods.Data;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class SubCommandAnnotationProcessor extends AnnotationProcessor<SubCommand> {

    private static final String CMD_SAME_SUB_METHOD = "cmd_not_valid_length";

    @Data
    private Command command;
    @Data
    private String[] arguments;
    @Data
    private CommandSender sender;
    @Data
    private List<String> annotations;

    @Override
    public boolean supply(Object executor, Method method) throws Exception {
        super.supply(executor, method);
        String value = getSerializedValue(method);
        if(annotations.contains(value)){
            Messages.create(CMD_SAME_SUB_METHOD).FAILURE().replaceNext(command::getName).send(sender);
            return true;
        }
        annotations.add(value);
        String[] annotationValues = value.split(" ");
        arguments = getSetupMessageArray(annotationValues, arguments);
        if(annotationValues.length == arguments.length && isMatch(annotationValues, arguments)){
            Object[] order;
            try{
                order = Parser.getInstance().fillParameters(sender, annotationValues, method.getParameterTypes(), arguments);
            }catch (ParserException exception){
                Messages.create(exception.getMessage()).WARNING().send(sender);
                return true;
            }

            if(ArrayUtils.isEmpty(order)) return (boolean) method.invoke(executor, sender);
            else return (boolean) MethodUtils.invokeMethod(executor, method.getName(), order,
                    method.getParameterTypes());
        }
        return false;
    }

    private boolean isMatch(String[] annotation, String[] src){
        boolean match = false;
        for(int i = 0; i < annotation.length; i++){
            String annotationString = annotation[i];
            if(!annotationString.startsWith("{") && !annotationString.endsWith("}")){
                if(!annotationString.equals(src[i])) return false;
                else match = true;
            }else{
                match = true;
            }
        }

        return match;
    }

    private String[] getSetupMessageArray(String[] subArgs, String[] input){
        if(input.length > subArgs.length && subArgs[subArgs.length - 1].equals("{message}")){
            String message = StringUtils.join(input, " ", subArgs.length - 1, input.length);
            input = Arrays.copyOf(input, subArgs.length);
            input[input.length - 1] = message;
        }

        return input;
    }

    private String getSerializedValue(Method method) {
        return method.getAnnotation(SubCommand.class).value();
    }
}
