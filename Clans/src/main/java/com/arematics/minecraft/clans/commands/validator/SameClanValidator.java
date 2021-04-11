package com.arematics.minecraft.clans.commands.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;
import com.arematics.minecraft.data.mode.model.ClanMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SameClanValidator extends ParameterValidator<ClanMember> {

    @Override
    public void supply(ClanMember result, List<Object> data) throws CommandProcessException {
        ClanMember entity = data.stream().filter(o -> o.getClass().isAssignableFrom(ClanMember.class))
                .map(o -> (ClanMember)o)
                .findFirst()
                .orElse(null);
        if(entity == null) throw new CommandProcessException("No other player found");
        if(result.getClanId() != entity.getClanId()) throw new CommandProcessException("Not in same clan");
    }
}
