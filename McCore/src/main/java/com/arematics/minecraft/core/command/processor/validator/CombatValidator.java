package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatValidator extends ParameterValidator<CorePlayer> {

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        System.out.println(result.hasPermission("team.infightcmd"));
        System.out.println(result.inFight());
        if(!result.hasPermission("team.infightcmd") && result.inFight()){
            throw new CommandProcessException("Not allowed in fight");
        }
    }
}
