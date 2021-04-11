package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.chat.Messenger;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.NotSelfValidator;
import com.arematics.minecraft.core.command.processor.validator.RequestValidator;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.MuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Perm(permission = "msg", description = "send a message")
public class MessageCommand extends CoreCommand {

    private final Messenger messenger;
    private final MuteService muteService;

    @Autowired
    public MessageCommand(Messenger messenger, MuteService muteService) {
        super("msg", true, "tell", "whisper", "w", "message");
        this.messenger = messenger;
        this.muteService = muteService;
    }

    @SubCommand("{player} {message}")
    public void message(CorePlayer player, @Validator(validators = {NotSelfValidator.class, RequestValidator.class}) CorePlayer target, String message) {
        if(muteService.isMuted(player.getUUID())) throw new CommandProcessException("Could not use message command while muted");
        messenger.sendMsg(player, target, message);
    }


}
