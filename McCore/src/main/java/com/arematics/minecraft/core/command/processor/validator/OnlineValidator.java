package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.data.global.model.User;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OnlineValidator extends ParameterValidator<User> {
    @Override
    public void supply(User result, List<Object> data) throws ParserException {
        if(Bukkit.getPlayer(result.getLastName()) == null)
            throw new ParserException("Player " + result.getLastName() + " is not online");
    }
}
