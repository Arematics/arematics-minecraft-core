package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendValidator extends ParameterValidator<User> {

    private final UserService service;

    @Autowired
    public FriendValidator(UserService userService){
        this.service = userService;
    }

    @Override
    public void supply(User result, List<Object> data) throws ParserException {
        CorePlayer player = data.stream().filter(o -> o.getClass().equals(CorePlayer.class))
                .map(o -> (CorePlayer)o)
                .findFirst()
                .orElse(null);
        if(player != null && service.getOrCreateUser(player).getFriends().contains(result))
            throw new ParserException("Player " + result.getLastName() + " is already a friend");
    }
}
