package com.arematics.minecraft.farmworld.commands;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.commands.WarpCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FarmWorldSpawnCommand extends SpawnCommand {

    @Autowired
    public FarmWorldSpawnCommand(WarpCommand warpCommand, WarpService warpService) {
        super(warpCommand, warpService);
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        try {
            Warp warp = getWarpService().getWarp("farmworld");
            this.getWarpCommand().teleport(sender, warp, false);
        } catch (Exception e) {
            sender.warn("Farmworld spawn was not set yet").handle();
        }
    }
}
