package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.command.processor.SubAnnotationProcessor;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.processor.methods.CommonData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CoreCommand implements CommandExecutor {

    private static final String CMD_NO_PERMS = "cmd_noperms";
    private static final String CMD_FAILURE = "cmd_failure";

    public abstract String[] getCommandNames();

    public abstract boolean matchAnyAccess();

    private final List<CommandAccess> accesses = new ArrayList<CommandAccess>(){{
       add(new RangAccess());
    }};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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
                if(isDefault) {
                    if (method.isAnnotationPresent(Default.class))
                        return (boolean) method.invoke(this, sender);
                }else if(method.isAnnotationPresent(Sub.class)){
                    if(new SubAnnotationProcessor().addDataPack(dataPack).supply(this, method)) return true;
                }
            }
        }catch (Exception exception){
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
