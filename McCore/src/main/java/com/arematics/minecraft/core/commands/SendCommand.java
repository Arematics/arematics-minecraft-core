package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "team.players.sendTo", description = "Send players to another server")
public class SendCommand extends CoreCommand {

    public SendCommand(){
        super("send");
    }

    @SubCommand("all {targetServer}")
    public void sendAllOnlineToServer(CorePlayer sender, String targetServer) {
        sender.info("Players are sent to the server").handle();
        server.getOnline().forEach(player -> {
            if(!player.hasPermission("team.players.sendTo")) MessagingUtils.sendToServer(player, targetServer);
        });
    }

    @SubCommand("{player} {targetServer}")
    public void subMethodNameReplace(CorePlayer sender, CorePlayer player, String targetServer) {
        sender.info("Player is sent to the server").handle();
        MessagingUtils.sendToServer(player, targetServer);
    }
}
