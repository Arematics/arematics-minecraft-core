package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.Messenger;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Perm(permission = "msg", description = "send a message")
public class MessageCommand extends CoreCommand {

    private final Messenger messenger;

    @Autowired
    public MessageCommand(Messenger messenger) {
        super("msg", "tell", "whisper", "w", "message");
        this.messenger = messenger;
        registerLongArgument("args");
    }

    @SubCommand("{player} {message}")
    public void message(CorePlayer player, CorePlayer target, String message) {
       messenger.sendMsg(player, target, message);
    }


}
