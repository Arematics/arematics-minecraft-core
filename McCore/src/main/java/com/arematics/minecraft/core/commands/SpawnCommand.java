package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
@Perm(permission = "spawn", description = "teleports to spawn")
public class SpawnCommand extends CoreCommand {

    private final WarpCommand warpCommand;
    private final WarpService warpService;

    @Autowired
    public SpawnCommand(WarpCommand warpCommand, WarpService warpService) {
        super("spawn");
        this.warpCommand = warpCommand;
        this.warpService = warpService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        try {
            Warp warp = warpService.getWarp("spawn");
            this.warpCommand.teleport(sender, warp, false);
        } catch (Exception e) {
            sender.warn("Spawn was not set yet").handle();
        }

    }
}
