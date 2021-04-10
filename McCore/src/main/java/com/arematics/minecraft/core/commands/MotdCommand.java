package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.ServerProperties;
import com.arematics.minecraft.data.service.ServerPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "server.modify.motd", description = "Change Server Motd")
public class MotdCommand extends CoreCommand {

    private final ServerPropertiesService serverPropertiesService;

    @Autowired
    public MotdCommand(ServerPropertiesService serverPropertiesService){
        super("motd", true);
        this.serverPropertiesService = serverPropertiesService;
    }

    @SubCommand("{line} {message}")
    public void refreshMotd(CorePlayer sender, Integer line, String message) {
        ServerProperties properties = new ServerProperties("motd_" + line, message.replaceAll("&", "§"));
        serverPropertiesService.save(properties);
        sender.info("Changed motd").handle();
    }
}
