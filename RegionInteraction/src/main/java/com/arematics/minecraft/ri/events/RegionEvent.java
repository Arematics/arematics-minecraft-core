package com.arematics.minecraft.ri.events;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.ri.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class RegionEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final ProtectedRegion region;
    private final MovementWay movement;
    private final CorePlayer player;

    public HandlerList getHandlers() {
        return handlerList;
    }
}
