package com.arematics.minecraft.homes.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DelHomeCommand extends CoreCommand {

    private final HomeCommand homeCommand;

    @Autowired
    public DelHomeCommand(HomeCommand homeCommand){
        super("delhome");
        this.homeCommand = homeCommand;
    }

    @SubCommand("{name}")
    public void deleteHomeCommand(CorePlayer sender, String name) {
        homeCommand.deleteHome(sender, name);
    }
}
