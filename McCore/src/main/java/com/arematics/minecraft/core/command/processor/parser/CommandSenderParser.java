package com.arematics.minecraft.core.command.processor.parser;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

public abstract class CommandSenderParser<T> extends CommandParameterParser<T> {

    public final T processParse(CommandSender sender, Parameter parameter, List<Object> data) throws CommandProcessException {
        T parsed = parse(sender);
        postParse(parsed, parameter, data);
        return parsed;
    }

    public abstract T parse(CommandSender sender) throws CommandProcessException;
}
