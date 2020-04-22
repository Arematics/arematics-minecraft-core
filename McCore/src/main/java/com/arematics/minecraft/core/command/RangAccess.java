package com.arematics.minecraft.core.command;

import org.bukkit.command.CommandSender;

public class RangAccess implements CommandAccess{

    @Override
    public boolean hasAccess(CommandSender sender, String cmd) {
        return true;
    }
}
