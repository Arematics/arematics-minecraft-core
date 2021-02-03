package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.BodyLocation;
import com.arematics.minecraft.guns.calculation.Bullet;
import com.arematics.minecraft.guns.events.BulletHitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class BulletHitListener implements Listener {

    @EventHandler
    public void onBulletHit(BulletHitEvent event){
        Bullet bullet = event.getBullet();
        CorePlayer damaged = event.getDamaged();
        BodyLocation hitLocation = event.getHitLocation();
        event.setFinalDamage(bullet.getDamage());
        damaged.warn("Hit from " + bullet.getShooter().getPlayer().getName()).handle();
    }
}
