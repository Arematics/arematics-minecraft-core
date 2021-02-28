package com.arematics.minecraft.core.events;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AsyncChatMessageEvent extends PlayerEvent{

    private final String message;
    private boolean cancelled;

    public AsyncChatMessageEvent(CorePlayer player, String message) {
        super(player, true);
        this.message = message;
    }
}
