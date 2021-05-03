package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.world.InteractHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatValidator extends ParameterValidator<CorePlayer> {

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        if(!result.hasPermission("team.infightcmd") && result.handle(InteractHandler.class).inFight()){
            throw new CommandProcessException("Not allowed in fight");
        }
    }
}
