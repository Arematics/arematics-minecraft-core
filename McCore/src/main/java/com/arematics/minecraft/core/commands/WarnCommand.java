package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.data.service.WarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "team.punishment.warn", description = "Warn a player")
public class WarnCommand extends CoreCommand {

    private final WarnService warnService;

    @Autowired
    public WarnCommand(WarnService warnService){
        super("warn");
        this.warnService = warnService;
    }

    
}
