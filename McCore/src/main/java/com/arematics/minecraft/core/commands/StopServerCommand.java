package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "*", description = "Stop the server")
public class StopServerCommand extends CoreCommand {

    @Autowired
    public StopServerCommand(){
        super("shutdown", true);
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.warn("Server will be shutdown, saving data");
        server.getOnline().forEach(player -> MessagingUtils.sendToServer(player, "lobbyone"));
    }
}
