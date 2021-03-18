package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class FriendValidator extends ParameterValidator<User> {

    private final FriendService friendService;

    @Override
    public void supply(User result, List<Object> data) throws CommandProcessException {
        CorePlayer player = data.stream().filter(o -> o.getClass().equals(CorePlayer.class))
                .map(o -> (CorePlayer)o)
                .findFirst()
                .orElse(null);
        if(player != null && friendService.isFriended(player.getUUID(), result.getUuid()))
            throw new CommandProcessException("Player " + result.getLastName() + " is already a friend");
    }
}
