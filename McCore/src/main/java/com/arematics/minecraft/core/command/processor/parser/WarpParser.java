package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.commands.WarpCommand;
import com.arematics.minecraft.data.mode.model.Warp;

import com.arematics.minecraft.data.service.WarpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarpParser extends CommandParameterParser<Warp> {

    private final WarpCommand warpCommand;
    private final WarpService service;

    @Autowired
    public WarpParser(WarpCommand warpCommand, WarpService service) {
        this.warpCommand = warpCommand;
        this.service = service;
    }

    @Override
    public Warp parse(String value) throws CommandProcessException {
        try{
            return service.getWarp(value);
        }catch (RuntimeException re){
            throw new CommandProcessException(re.getMessage());
        }
    }
}
