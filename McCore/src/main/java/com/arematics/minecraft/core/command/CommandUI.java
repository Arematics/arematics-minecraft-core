package com.arematics.minecraft.core.command;

import org.bukkit.command.CommandSender;

import java.util.function.Function;

public interface CommandUI {
    CommandAccept setUI(Function<CommandSender, Boolean> onUI);
}
