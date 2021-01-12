package com.arematics.minecraft.strongholds.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import com.arematics.minecraft.ri.events.RegionLeftEvent;
import com.arematics.minecraft.strongholds.StrongholdBoot;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureLeaveEvent;
import com.arematics.minecraft.strongholds.events.StrongholdEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdLeaveEvent;
import com.arematics.minecraft.strongholds.commands.parser.StrongholdParser;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class StrongholdRegionsListener implements Listener {

    private final StrongholdBoot plugin;
    private final StrongholdParser parser;

    @Autowired
    public StrongholdRegionsListener(StrongholdParser strongholdParser){
        this.plugin = Boots.getBoot(StrongholdBoot.class);
        this.parser = strongholdParser;
    }

    @EventHandler
    public void onEntered(RegionEnteredEvent event){
        CorePlayer player = event.getPlayer();
        ProtectedRegion region = event.getRegion();

        if(region.getId().startsWith("stronghold_outer"))
            callStrongholdEvent(region, (sh) -> new StrongholdEnterEvent(region, player, event.getMovement(), sh));

        if(region.getId().startsWith("stronghold_inner"))
            callStrongholdEvent(region, (sh) -> new StrongholdCaptureEnterEvent(region, player, event.getMovement(), sh));

    }

    @EventHandler
    public void onLeft(RegionLeftEvent event){
        CorePlayer player = event.getPlayer();
        ProtectedRegion region = event.getRegion();

        if(region.getId().startsWith("stronghold_outer"))
            callStrongholdEvent(region, (sh) -> new StrongholdLeaveEvent(region, player, event.getMovement(), sh));

        if(region.getId().startsWith("stronghold_inner"))
            callStrongholdEvent(region, (sh) -> new StrongholdCaptureLeaveEvent(region, player, event.getMovement(), sh));
    }

    private <T extends BaseEvent> void callStrongholdEvent(ProtectedRegion region, Function<Stronghold, T> event){
        String shId = region.getId().split("_")[2];
        try{
            Stronghold stronghold = this.parser.parse(shId);
            T callEvent = event.apply(stronghold);
            this.plugin.callEvent(callEvent);
        }catch (Exception ignore){}
    }
}
