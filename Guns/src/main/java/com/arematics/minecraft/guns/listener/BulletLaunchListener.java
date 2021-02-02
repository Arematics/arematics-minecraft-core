package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.guns.calculation.Bullet;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.springframework.stereotype.Component;

@Component
public class BulletLaunchListener implements Listener {

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getShooter() instanceof Player){
            Snowball snowball = (Snowball) projectile;
            CorePlayer player = CorePlayer.get((Player) snowball.getShooter());
            Weapon weapon = new Weapon("GLOCK 19", WeaponType.MACHINE_GUN, (byte)4, (byte)1);
            Bullet bullet = new Bullet(snowball.getUniqueId(), player, weapon, weapon.getTotalDamage() / weapon.getBullets());
            Bullet.register(bullet);
        }
    }
}
