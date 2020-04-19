package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.Engine;
import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CoreCommand implements CommandExecutor {

    private final static String NO_PERMS_KEY = "cmd_noperms";

    public abstract String[] getCommandNames();

    public abstract boolean validate(CommandSender sender, Command command, String label, String[] subs, int length);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(canAccessCommand(commandSender, command))
            return validate(commandSender, command, s, strings, strings.length);
        else
            LanguageAPI.sendWarning((Player) commandSender, NO_PERMS_KEY);
        return true;
    }

    public boolean canAccessCommand(CommandSender sender, Command command){
        return sender.getName().equals("Klysma");
    }
}
