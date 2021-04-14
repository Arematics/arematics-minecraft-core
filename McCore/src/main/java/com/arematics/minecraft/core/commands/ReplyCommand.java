package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.chat.Messenger;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.NotMutedValidator;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplyCommand extends CoreCommand {

    private final Messenger messenger;

    @Autowired
    public ReplyCommand(Messenger messenger) {
        super("reply", "r");
        this.messenger = messenger;
    }

    @SubCommand("{message}")
    public void reply(@Validator(validators = NotMutedValidator.class) CorePlayer player,
                      String message) {
        messenger.reply(player, message);
    }


}
