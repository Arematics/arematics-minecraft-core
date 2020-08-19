package com.arematics.minecraft.meta.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Gets called everytime armor is losing durability or tools losing durability.
 */
public class DurabilityLoseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ItemStack ITEMSTACK;
    public int LOSE_AMOUNT;

    private boolean cancelled;

    public DurabilityLoseEvent(ItemStack itemStack, int loseAmount){
        this.ITEMSTACK = itemStack;
        this.LOSE_AMOUNT = loseAmount;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
