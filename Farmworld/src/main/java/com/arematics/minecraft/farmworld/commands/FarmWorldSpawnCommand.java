package com.arematics.minecraft.farmworld.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FarmWorldSpawnCommand extends CoreCommand {

    private final SpawnCommand spawnCommand;

    @Autowired
    public FarmWorldSpawnCommand(SpawnCommand spawnCommand) {
        super("spawn");
        this.spawnCommand = spawnCommand;
        this.spawnCommand.currentTeleport = "farmworld";
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        spawnCommand.onDefaultExecute(sender);
    }
}
