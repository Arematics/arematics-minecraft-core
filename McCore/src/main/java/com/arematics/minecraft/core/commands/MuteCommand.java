package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import org.springframework.stereotype.Component;

@Component
public class MuteCommand extends CoreCommand {

    public MuteCommand(){
        super("mute");
    }
}
