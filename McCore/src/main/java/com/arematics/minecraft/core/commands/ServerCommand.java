package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerCommand extends CoreCommand {

    @Autowired
    public ServerCommand(){
        super("connect", true, "server");
    }

    @SubCommand("{name}")
    public void changeServer(CorePlayer sender, String name) {
        if(name.equals("farmworld")) return;
        MessagingUtils.sendToServer(sender, name);
    }

}
