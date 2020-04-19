package com.arematics.minecraft.core.command;

import org.bukkit.command.CommandSender;

public interface CommandAccess {

    boolean hasAccess(CommandSender sender, String cmd);
}
