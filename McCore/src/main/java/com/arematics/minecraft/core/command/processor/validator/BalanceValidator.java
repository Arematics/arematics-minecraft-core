package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceValidator extends ParameterValidator<Long> {

    @Override
    public void supply(Long result, List<Object> data) throws CommandProcessException {
        CorePlayer player = data.stream().filter(o -> o.getClass().equals(CorePlayer.class))
                .map(o -> (CorePlayer)o)
                .findFirst()
                .orElse(null);
        if(player != null && player.getStats().getCoins() < result)
            throw new CommandProcessException("You do not have enough coins to effort this");
    }
}
