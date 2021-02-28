package com.arematics.minecraft.strongholds.listener;

import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureController;
import com.arematics.minecraft.strongholds.capture.model.StrongholdCapture;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureLeaveEvent;
import com.arematics.minecraft.strongholds.events.StrongholdEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdLeaveEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class StrongholdScoreboardListener implements Listener {

    private final StrongholdCaptureController controller;

    @EventHandler
    public void onStrongholdEnter(StrongholdEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        final BoardHandler handler = player.getBoard().getOrAddBoard("stronghold", "§4§lSTRONGHOLD");
        handler.addEntryData("Name", "§c", "§7" + stronghold.getId())
                .addEntryData("Platz 3", "§c", "§c" + "Not found")
                .addEntryData("Platz 2", "§c", "§c" + "Not found")
                .addEntryData("Platz 1", "§c", "§c" + "Not found")
                .addEntryData("Time", "§c", "§a" + "§fNicht aktiv")
                .show();
    }

    @EventHandler
    public void onStrongholdCaptureEnter(StrongholdCaptureEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCapture capture = controller.getCapture();
        String captureResult = capture == null ? "§cDisabled" : capture.getStronghold().equals(stronghold) ? "§aEnabled" : "§cDisabled";
        final BoardHandler handler = player.getBoard().getOrAddBoard("stronghold-capture", "§4§lSH CAPTURE");
        handler.addEntryData("Capture", "§c", "§a" + captureResult)
                .addEntryData("Platz 3", "§c", "§c" + "Not found")
                .addEntryData("Platz 2", "§c", "§c" + "Not found")
                .addEntryData("Platz 1", "§c", "§c" + "Not found")
                .addEntryData("Time", "§c", "§a" + "§fNicht aktiv")
                .show();
    }

    @EventHandler
    public void onStrongholdLeave(StrongholdLeaveEvent event){
        CorePlayer player = event.getPlayer();
        player.getBoard().getBoard("stronghold").remove();
        player.getBoard().getBoard("main").toggle();
    }

    @EventHandler
    public void onStrongholdCaptureLeave(StrongholdCaptureLeaveEvent event){
        CorePlayer player = event.getPlayer();
        player.getBoard().getBoard("stronghold-capture").remove();
        player.getBoard().getOrAddBoard("stronghold", "§4§lSTRONGHOLD").toggle();
    }
}
