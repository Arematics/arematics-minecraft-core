package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.annotations.*;
import com.arematics.minecraft.core.command.processor.SubCommandAnnotationProcessor;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.CommonData;
import com.arematics.minecraft.core.processor.methods.MethodProcessorEnvironment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CoreCommand implements CommandExecutor {

    private static final String CMD_NO_PERMS = "cmd_noperms";
    private static final String CMD_FAILURE = "cmd_failure";

    private final String[] commandNames;
    private final boolean matchAnyAccess;

    public CoreCommand() {
        boolean anyAccess = false;
        String[] names = {};

        for(Annotation annotation : this.getClass().getAnnotations()){
            if(annotation.annotationType() == AnyAccess.class)
               anyAccess = true;
            if(annotation.annotationType() == PluginCommand.class)
                names = ((PluginCommand)annotation).names();
        }
        this.commandNames = names;
        this.matchAnyAccess = anyAccess;

        this.registerStandards();
    }

    private void registerStandards(){
        this.accesses.add(new RangAccess());
        this.processors.put(SubCommand.class, new SubCommandAnnotationProcessor());

        try {
            for(Annotation annotation : this.getClass().getAnnotations()){
                if(annotation.annotationType() == PluginCommand.class) {
                    Class<? extends AnnotationProcessor<?>>[] processors = ((PluginCommand)annotation).processors();
                    for (Class<? extends AnnotationProcessor<?>> processor : processors) {
                        AnnotationProcessor<?> instance = processor.newInstance();
                        this.processors.put(instance.get(), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Defining all Command Names Spigot should register for this executor
     * @return Command Names
     */
    public final String[] getCommandNames(){
        return this.commandNames;
    }

    /**
     * Defined Accesses protects the command from being executed by someone isn't allowed to use this command.
     * This method defines if all Accesses must be allowed for the execute ore only one access.
     * @return If only one access is enough = return true else return false
     */
    public final boolean matchAnyAccess(){
        return this.matchAnyAccess;
    }

    private final List<CommandAccess> accesses = new ArrayList<>();

    private final Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors = new HashMap<>();

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(canAccessCommand(commandSender, command))
            return process(commandSender, command, strings);
        else
            Messages.create(CMD_NO_PERMS).WARNING().send(commandSender);
        return true;
    }

    private boolean process(CommandSender sender, Command command, String[] args){
        boolean isDefault = args.length == 0;
        List<String> annos = new ArrayList<>();
        Map<String, Object> dataPack = new HashMap<>();
        dataPack.put(CommonData.COMMAND_SENDER.toString(), sender);
        dataPack.put("annotations", annos);
        dataPack.put(CommonData.COMMAND_ARGUEMNTS.toString(), args);
        dataPack.put(CommonData.COMMAND.toString(), command);
        try{
            for (final Method method : this.getClass().getDeclaredMethods()){
                MethodProcessorEnvironment environment = new MethodProcessorEnvironment(this, method, dataPack);
                if(isDefault) {
                    if (method.isAnnotationPresent(Default.class))
                        return (boolean) method.invoke(this, sender);
                } else {
                    for(Map.Entry<Class<? extends Annotation>, AnnotationProcessor<?>> processorEntry : this.processors.entrySet()){
                        if(method.isAnnotationPresent(processorEntry.getKey()))
                            if (processorEntry.getValue().setEnvironment(environment).supply(this, method)) return true;
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
            Messages.create(CMD_FAILURE).FAILURE().send(sender);
        }
        return true;
    }

    public boolean canAccessCommand(CommandSender sender, Command command){
        if(matchAnyAccess()) return getAccesses().stream().anyMatch(access -> access.hasAccess(sender, command.getName()));
        else return getAccesses().stream().allMatch(access -> access.hasAccess(sender, command.getName()));
    }

    public List<CommandAccess> getAccesses() {
        return accesses;
    }

}
