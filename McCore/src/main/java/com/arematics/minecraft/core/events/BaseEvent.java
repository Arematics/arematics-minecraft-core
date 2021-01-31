package com.arematics.minecraft.core.events;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public BaseEvent(boolean async){
        super(async);
    }
}
