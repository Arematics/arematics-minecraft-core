package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This validator also contains the online validator
 */
@Component
public class RequestValidator extends ParameterValidator<User> {

    private final UserService service;

    @Autowired
    public RequestValidator(UserService userService){
        this.service = userService;
    }

    @Override
    public void supply(User result, List<Object> data) throws CommandProcessException {
        OnlineValidator validator = Boots.getBoot(CoreBoot.class).getContext().getBean(OnlineValidator.class);
        validator.supply(result, data);
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

    private void isValid(CorePlayer player, User target) throws RuntimeException{
        User user = service.getOrCreateUser(player);
        CorePlayer targetPlayer = CorePlayer.get(Bukkit.getPlayer(target.getUuid()));
        targetPlayer.getRequestSettings().checkAllowed(user);
    }
}
