package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.command.Sub;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.CommonData;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class SubAnnotationProcessor extends AnnotationProcessor {

    private static final String CMD_SAME_SUB_METHOD = "cmd_not_valid_length";

    @Override
    public boolean supply(Object executing, Method method) throws Exception{
        CommandSender sender = (CommandSender) getData(CommonData.COMMAND_SENDER);
        List<String> annos = (List<String>) getData("annotations");
        String[] args = (String[]) getData(CommonData.COMMAND_ARGUEMNTS);
        Command command = (Command) getData(CommonData.COMMAND);

        String value = getSerializedValue(method);
        if(annos.contains(value)){
            Messages.create(CMD_SAME_SUB_METHOD).FAILURE().replaceNext(command::getName).send(sender);
            return true;
        }
        annos.add(value);
        String[] annotationValues = value.split(" ");
        args = getSetupMessageArray(annotationValues, args);
        if(annotationValues.length == args.length && isMatch(annotationValues, args)){
            Object[] order;
            try{
                order = Parser.getInstance().fillParameters(sender, annotationValues, method.getParameterTypes(), args);
            }catch (ParserException exception){
                Messages.create(exception.getMessage()).WARNING().send(sender);
                return true;
            }

            if(ArrayUtils.isEmpty(order)) return (boolean) method.invoke(executing, sender);
            else return (boolean) MethodUtils.invokeMethod(executing, method.getName(), order,
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
        return method.getAnnotation(Sub.class).value();
    }
}
