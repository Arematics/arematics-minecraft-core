package com.arematics.minecraft.strongholds.capture.controller;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import lombok.Data;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Controller;

@Data
@Controller
public class StrongholdCaptureController {

    private StrongholdCaptureRunner activeRunner;

    public void enableStronghold(Stronghold stronghold){
        this.activeRunner = new StrongholdCaptureRunner(stronghold);
        this.activeRunner.initRunner();
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .forEach(player -> player.info("Stronghold " + stronghold.getId() + " has been enabled").handle());
    }
}
