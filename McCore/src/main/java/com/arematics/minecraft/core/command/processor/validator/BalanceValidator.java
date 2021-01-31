package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.CurrencyEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceValidator extends ParameterValidator<Double> {

    @Override
    public void supply(Double result, List<Object> data) throws CommandProcessException {
        CurrencyEntity entity = data.stream().filter(o -> o.getClass().isAssignableFrom(CurrencyEntity.class))
                .map(o -> (CurrencyEntity)o)
                .findFirst()
                .orElse(null);
        try{
            if(entity == null) throw new CommandProcessException("Could not find parameter checking currency for");
        }catch (RuntimeException re){
            throw new CommandProcessException("You do not have enough coins to effort this");
        }
    }
}
