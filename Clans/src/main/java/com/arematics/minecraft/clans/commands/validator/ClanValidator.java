package com.arematics.minecraft.clans.commands.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.ParameterValidator;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.ClanMemberService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class ClanValidator extends ParameterValidator<CorePlayer> {

    private final ClanMemberService service;
    private final boolean negativeCheck;

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        try{
            service.getMember(result);
            if(negativeCheck) throw new CommandProcessException("You have a clan already");
        }catch (RuntimeException re){
            if(!negativeCheck) throw new CommandProcessException("You have no clan at the moment");
        }
    }
}
