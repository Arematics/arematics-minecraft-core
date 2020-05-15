package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.command.processor.Processor;
import com.arematics.minecraft.core.command.processor.ProcessorException;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.core.command.processor.parser.sender.PlayerOnly;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.processor.methods.commands.CommandAnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.commands.CommandProcessorData;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.server.CoreUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CoreCommand implements CommandExecutor {

    private final static String NO_PERMS_KEY = "cmd_noperms";
    private static final String NOT_VALID_LENGHT = "cmd_not_valid_length";

    public abstract String[] getCommandNames();

    public abstract CommandAnnotationProcessor[] defineExecutingProcessors(CommandProcessorData commandProcessorData);

    public abstract boolean matchAnyAccess();

    private final List<CommandAccess> accesses = new ArrayList<CommandAccess>(){{
       add(new RangAccess());
    }};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(canAccessCommand(commandSender, command))
            return process(commandSender, command, strings);
        else
            LanguageAPI.sendWarning((Player) commandSender, NO_PERMS_KEY);
        return true;
    }

    private boolean process(CommandSender sender, Command command, String[] args){
        boolean isDefault = args.length == 0;
        List<String> annos = new ArrayList<>();
        try{
            for (final Method method : this.getClass().getDeclaredMethods()){
                if(isDefault) {
                    if (method.isAnnotationPresent(Default.class)) {
                        return (boolean) method.invoke(this, sender);
                    }
                }else if(method.isAnnotationPresent(Sub.class)){
                    String value = getSerializedValue(method);
                    if(annos.contains(value)){
                        LanguageAPI.injectable("cmd_same_sub_method_exist")
                                .inject(command::getName)
                                .FAILURE()
                                .send((Player) sender);
                        return true;
                    }
                    annos.add(value);
                    String[] annotationValues = value.contains(" ") ? value.split(" ") : new String[]{value};
                    args = getSetupMessageArray(annotationValues, args);
                    if(annotationValues.length == args.length && isMatch(annotationValues, args)){
                        Object[] order;
                        try{
                            order = Parser.getInstance().fillParameters(sender, annotationValues, method.getParameterTypes(), args);
                        }catch (ParserException exception){
                            //String type = exception instanceof ProcessorException ? Config.FAILURE : Config.WARNING;
                            sender.sendMessage(Config.getInstance().getPrefix() +
                                    Config.getInstance().getHighlight(Config.WARNING).getColorCode() +
                                    exception.getMessage());
                            return true;
                        }

                        if(ArrayUtils.isEmpty(order)){
                            return (boolean) method.invoke(this, sender);
                        }
                        else return (boolean) MethodUtils.invokeMethod(this, method.getName(), order,
                                method.getParameterTypes());
                    }
                }
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
            LanguageAPI.injectable("cmd_failure")
                    .FAILURE()
                    .send((Player)sender);
        }
        return true;
    }

    private Object[] getParameters(CommandSender sender, String[] annotationValues, Method method, String[] input)
        throws ProcessorException, ParserException
    {
        Object[] order = new Object[]{};
        CommandProcessorData commandProcessorData = new CommandProcessorData(input, annotationValues);
        for(CommandAnnotationProcessor processor : defineExecutingProcessors(commandProcessorData))
            order = processor.process(method);
        return order;
    }

    private boolean isMatch(String[] annotation, String[] src){
        boolean match = false;
        for(int i = 0; i < annotation.length; i++){
            String annotationString = annotation[i];
            if(!annotationString.startsWith("{") && !annotationString.endsWith("}")){
                if(!annotationString.equals(src[i])) return false;
                else match = true;
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

    public boolean canAccessCommand(CommandSender sender, Command command){
        if(matchAnyAccess()) return getAccesses().stream().anyMatch(access -> access.hasAccess(sender, command.getName()));
        else return getAccesses().stream().allMatch(access -> access.hasAccess(sender, command.getName()));
    }

    public List<CommandAccess> getAccesses() {
        return accesses;
    }
}
