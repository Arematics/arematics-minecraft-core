package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;

import java.lang.reflect.Parameter;
import java.util.List;

public abstract class CommandSenderParser<T> extends CommandParameterParser<T> {

    public final T processParse(CorePlayer sender, Parameter parameter, List<Object> data) throws CommandProcessException {
        T parsed = parse(sender);
        postParse(parsed, parameter, data);
        return parsed;
    }

    public abstract T parse(CorePlayer sender) throws CommandProcessException;
}
