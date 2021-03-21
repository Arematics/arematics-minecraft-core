package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserParser extends CommandParameterParser<User> {

    private final UserService service;

    @Autowired
    public UserParser(UserService userService){
        this.service = userService;
    }

    @Override
    public User parse(String value) throws CommandProcessException {
        try{
            return this.service.findByName(value);
        }catch (RuntimeException re){
            try{
                return this.service.getUserByUUID(UUID.fromString(value));
            }catch (RuntimeException re2){
                throw new CommandProcessException("user_never_played_here", injector -> injector
                        .DEFAULT()
                        .replace("name", value));
            }
        }
    }
}
