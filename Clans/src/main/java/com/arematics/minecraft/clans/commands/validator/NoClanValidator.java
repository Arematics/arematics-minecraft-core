package com.arematics.minecraft.clans.commands.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.service.ClanMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoClanValidator extends ParameterValidator<CorePlayer> {

    private final ClanValidator validator;

    @Autowired
    public NoClanValidator(ClanMemberService service) {
        this.validator = new ClanValidator(service, true);
    }

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        validator.supply(result, data);
    }
}
