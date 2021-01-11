package com.arematics.minecraft.strongholds.listener;

import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureLeaveEvent;
import com.arematics.minecraft.strongholds.events.StrongholdEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class StrongholdScoreboardListener implements Listener {

    @EventHandler
    public void onStrongholdEnter(StrongholdEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        final BoardHandler handler = player.getBoard().getOrAddBoard("stronghold", "§4§lSTRONGHOLD");
        handler.addEntryData("Name", "§c", "§7" + stronghold.getId())
                .addEntryData("Time", "§c", "§a" + "§fNicht aktiv")
                .show();
    }

    @EventHandler
    public void onStrongholdCaptureEnter(StrongholdCaptureEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        final BoardHandler handler = player.getBoard().getOrAddBoard("stronghold-capture", "§4§lSH CAPTURE");
        handler.addEntryData("Name", "§c", "§7" + stronghold.getId())
                .addEntryData("Capture", "§c", "§a" + "Enabled")
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
