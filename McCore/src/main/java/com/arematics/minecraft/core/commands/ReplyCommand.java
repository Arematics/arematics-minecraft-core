package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.Messenger;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Perm(permission = "reply", description = "reply to a message")
public class ReplyCommand extends CoreCommand {

    private final Messenger messenger;

    @Autowired
    public ReplyCommand(Messenger messenger) {
        super("reply", "r");
        this.messenger = messenger;
        registerLongArgument("args");
    }

    @SubCommand("{message}")
    public void reply(CorePlayer player, String message) {
        messenger.reply(player, message);
    }


}
