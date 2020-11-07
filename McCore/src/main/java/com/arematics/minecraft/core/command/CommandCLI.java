package com.arematics.minecraft.core.command;

import org.bukkit.command.CommandSender;

import java.util.function.Function;

public interface CommandCLI {
    CommandUI setCLI(Function<CommandSender, Boolean> onCli);
}
