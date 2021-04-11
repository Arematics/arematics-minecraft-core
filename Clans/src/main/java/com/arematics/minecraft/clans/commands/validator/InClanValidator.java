package com.arematics.minecraft.clans.commands.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.ClanMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InClanValidator extends ParameterValidator<User> {

    private final ClanValidator validator;

    @Autowired
    public InClanValidator(ClanMemberService service) {
        this.validator = new ClanValidator(service, false);
    }

    @Override
    public void supply(User result, List<Object> data) throws CommandProcessException {
        validator.supply(result, data);
    }
}
