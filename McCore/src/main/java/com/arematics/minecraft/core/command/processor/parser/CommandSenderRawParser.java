package com.arematics.minecraft.core.command.processor.parser;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

@Component
public class CommandSenderRawParser extends CommandSenderParser<CommandSender> {

    @Override
    public CommandSender parse(String value) throws CommandProcessException {
        return Bukkit.getConsoleSender();
    }

    @Override
    public CommandSender parse(CommandSender sender) throws CommandProcessException {
        return sender;
    }
}
