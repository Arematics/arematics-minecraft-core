package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.guns.calculation.BodyLocation;
import com.arematics.minecraft.guns.calculation.Bullet;
import com.arematics.minecraft.guns.events.BulletHitEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class PlayerHitBySnowballListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getDamager() instanceof Snowball){
                try{
                    Bullet bullet = Bullet.findBulletById(event.getDamager().getUniqueId());
                    CorePlayer player = CorePlayer.get((Player) event.getEntity());
                    Location hitPoint = event.getDamager().getLocation();
                    Location playerLocation = event.getEntity().getLocation();
                    BodyLocation location;
                    if(hitPoint.getY() > playerLocation.getY() + 1.55){
                        location = BodyLocation.HEAD;
                    }else if(hitPoint.getY() < playerLocation.getY() + 0.9){
                        location = BodyLocation.BODY;
                    }else location = BodyLocation.LEGS;
                    BulletHitEvent hitEvent = new BulletHitEvent(bullet, player, location);
                    Bukkit.getServer().getPluginManager().callEvent(hitEvent);
                    event.setCancelled(true);
                    ArematicsExecutor.syncDelayed(() -> tearDownDamage(player, hitEvent),
                            5 + random.nextInt(5), TimeUnit.MILLISECONDS);
                    Bullet.remove(hitEvent.getBullet().getBulletId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void tearDownDamage(CorePlayer hit, BulletHitEvent event){
        if(!hit.getPlayer().isDead()) {
            hit.getPlayer().damage(event.getFinalDamage(), event.getBullet().getShooter().getPlayer());
            hit.getPlayer().setNoDamageTicks(1);
        }
    }
}
