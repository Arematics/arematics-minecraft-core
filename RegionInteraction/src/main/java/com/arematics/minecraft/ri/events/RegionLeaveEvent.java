package com.arematics.minecraft.ri.events;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.ri.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.Cancellable;

@Getter
@ToString
public class RegionLeaveEvent extends RegionEvent implements Cancellable {
    private boolean cancelled = false;
    private boolean cancellable;

    public RegionLeaveEvent(ProtectedRegion region, MovementWay movement, CorePlayer player) {
        super(region, movement, player);
        if (movement == MovementWay.SPAWN || movement == MovementWay.DISCONNECT) {
            this.cancellable = false;
        }
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if(this.cancellable) this.cancelled = cancelled;
    }
}
