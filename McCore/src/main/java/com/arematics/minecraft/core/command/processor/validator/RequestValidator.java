package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.RequestHandler;
import com.arematics.minecraft.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This validator also contains the online validator
 */
@Component
public class RequestValidator extends ParameterValidator<CorePlayer> {

    private final UserService service;

    @Autowired
    public RequestValidator(UserService userService){
        this.service = userService;
    }

    @Override
    public void supply(CorePlayer result, List<Object> data) throws CommandProcessException {
        CorePlayer player = data.stream().filter(o -> o.getClass().equals(CorePlayer.class))
                .map(o -> (CorePlayer)o)
                .findFirst()
                .orElse(null);
        try{
            isValid(player, result);
        }catch (RuntimeException re){
            throw new CommandProcessException(re.getMessage());
        }
    }

    private void isValid(CorePlayer player, CorePlayer target) throws RuntimeException{
        target.handle(RequestHandler.class).checkAllowed(player);
    }
}
