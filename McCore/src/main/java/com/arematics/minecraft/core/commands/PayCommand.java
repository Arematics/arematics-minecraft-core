package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.RequestValidator;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class PayCommand extends CoreCommand {

    public PayCommand(){
        super("pay", true);
    }

    @SubCommand("{target} {amount}")
    public void payMoneyTo(CorePlayer sender,
                           @Validator(validators = RequestValidator.class) CorePlayer target,
                           Double amount) {
        sender.interact().dispatchCommand("money pay " + target.getName() + " " + amount);
    }
}
