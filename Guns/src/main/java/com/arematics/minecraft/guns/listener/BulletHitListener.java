package com.arematics.minecraft.guns.listener;

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
        event.setFinalDamage(bullet.getDamage());
        event.getBullet().getShooter()
                .info("Bullet hit " + event.getDamaged().getPlayer().getName() + " with " + bullet.getDamage() + " total damage")
                .handle();
    }
}
