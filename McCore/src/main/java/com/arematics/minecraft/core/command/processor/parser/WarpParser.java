package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.commands.WarpCommand;
import com.arematics.minecraft.data.mode.model.Warp;
import org.springframework.stereotype.Component;

@Component
public class WarpParser extends CommandParameterParser<Warp> {
    @Override
    public Warp parse(String value) throws CommandProcessException {
        Warp warp = WarpCommand.getWarps().get(value);
        if (warp == null) throw new CommandProcessException("Warp with name " + value + " not exist!");
        return warp;
    }
}
