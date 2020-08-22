package com.arematics.minecraft.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class BaseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
