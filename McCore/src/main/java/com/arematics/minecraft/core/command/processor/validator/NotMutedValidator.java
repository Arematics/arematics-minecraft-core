package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.MuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class NotMutedValidator extends ParameterValidator<CorePlayer> {
    private final MuteService muteService;

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        if(muteService.isMuted(result.getUUID()))
            throw new CommandProcessException("You are muted an couldn't do this at the moment");
    }
}
