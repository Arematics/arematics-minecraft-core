package com.arematics.minecraft.homes.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetHomeCommand extends CoreCommand {

    private final HomeCommand homeCommand;

    @Autowired
    public SetHomeCommand(HomeCommand homeCommand){
        super("sethome");
        this.homeCommand = homeCommand;
    }

    @SubCommand("{name}")
    public void setHome(CorePlayer sender, String name) {
        this.homeCommand.setHome(sender, name);
    }
}
