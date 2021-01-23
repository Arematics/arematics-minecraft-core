package com.arematics.minecraft.ri.events;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.ri.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.ToString;


@ToString(callSuper = true)
public class RegionEnteredEvent extends RegionEvent{

    public RegionEnteredEvent(ProtectedRegion region, MovementWay movement, CorePlayer player) {
        super(region, movement, player);
    }
}
