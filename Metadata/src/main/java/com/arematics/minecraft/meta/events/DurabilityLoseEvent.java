package com.arematics.minecraft.meta.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Gets called everytime armor is losing durability or tools losing durability.
 */
public class DurabilityLoseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player PLAYER;
    public int SLOT;
    public ItemStack ITEMSTACK;
    public int LOSE_AMOUNT;

    public DurabilityLoseEvent(Player player, int slot, ItemStack itemStack, int loseAmount){
        this.PLAYER = player;
        this.SLOT = slot;
        this.ITEMSTACK = itemStack;
        this.LOSE_AMOUNT = loseAmount;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
