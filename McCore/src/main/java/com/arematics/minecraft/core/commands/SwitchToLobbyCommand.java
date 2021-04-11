package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwitchToLobbyCommand extends CoreCommand {

    private final ServerCommand serverCommand;

    @Autowired
    public SwitchToLobbyCommand(ServerCommand serverCommand){
        super("lobby", true, "hub");
        this.serverCommand = serverCommand;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        this.serverCommand.changeServer(sender, "lobbyone");
    }
}
