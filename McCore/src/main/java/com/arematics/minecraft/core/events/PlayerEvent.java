package com.arematics.minecraft.core.events;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerEvent extends BaseEvent {
    private final CorePlayer player;
}
