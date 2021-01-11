package com.arematics.minecraft.strongholds.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureController;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureRunner;
import com.arematics.minecraft.strongholds.events.StrongholdEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrongholdRunnerListener implements Listener {

    private final StrongholdCaptureController controller;

    @Autowired
    public StrongholdRunnerListener(StrongholdCaptureController strongholdCaptureController){
        this.controller = strongholdCaptureController;
    }

    @EventHandler
    public void onStrongholdEnter(StrongholdEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCaptureRunner runner = this.controller.getActiveRunner();

        if(runner != null && runner.getCapture().getStronghold().equals(stronghold))
            runner.getCapture().getInStronghold().add(player);
    }

    @EventHandler
    public void onStrongholdLeave(StrongholdLeaveEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCaptureRunner runner = this.controller.getActiveRunner();

        if(runner != null && runner.getCapture().getStronghold().equals(stronghold))
            runner.getCapture().getInStronghold().remove(player);
    }
}
