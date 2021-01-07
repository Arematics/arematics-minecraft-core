package com.arematics.minecraft.strongholds.events;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.ri.MovementWay;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;

@Getter
public class StrongholdCaptureLeaveEvent extends RegionEnteredEvent {

    private final Stronghold stronghold;

    public StrongholdCaptureLeaveEvent(ProtectedRegion region, CorePlayer player, MovementWay movement, Stronghold stronghold) {
        super(region, movement, player);
        this.stronghold = stronghold;
    }
}
