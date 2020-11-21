package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.global.model.User;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OnlineValidator extends ParameterValidator<User> {
    @Override
    public void supply(User result, List<Object> data) throws CommandProcessException {
        if(Bukkit.getPlayer(result.getLastName()) == null)
            throw new CommandProcessException("Player " + result.getLastName() + " is not online");
    }
}
