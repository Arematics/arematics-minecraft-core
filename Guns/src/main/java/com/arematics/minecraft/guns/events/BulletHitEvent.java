package com.arematics.minecraft.guns.events;

import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.BodyLocation;
import com.arematics.minecraft.guns.calculation.Bullet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Getter
@Setter
@RequiredArgsConstructor
public class BulletHitEvent extends BaseEvent implements Cancellable {
    private final Bullet bullet;
    private final CorePlayer damaged;
    private final BodyLocation hitLocation;
    private double finalDamage;
    private boolean isCancelled;
}
