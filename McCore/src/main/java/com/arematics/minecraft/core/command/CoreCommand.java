package com.arematics.minecraft.core.command;

import org.bukkit.command.CommandExecutor;

public abstract class CoreCommand implements CommandExecutor {

    public abstract String[] getCommandNames();
}
