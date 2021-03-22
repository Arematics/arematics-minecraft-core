package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotSelfValidator extends ParameterValidator<CorePlayer> {
    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        CorePlayer player = data.stream().filter(o -> o.getClass().equals(CorePlayer.class))
                .map(o -> (CorePlayer)o)
                .findFirst()
                .orElse(null);
        if(player != null)
            if(player.getUUID().equals(result.getUUID())) throw new CommandProcessException("Targeting yourself is not allowed");
    }
}
