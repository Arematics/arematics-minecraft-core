package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.guns.calculation.Bullet;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BulletLaunchListener implements Listener {

    private final WeaponService weaponService;

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getShooter() instanceof Player){
            Snowball snowball = (Snowball) projectile;
            CorePlayer player = CorePlayer.get((Player) snowball.getShooter());
            CoreItem hand = player.getItemInHand();
            if(hand != null && hand.getMeta().hasKey("weapon") != null){
                String weaponId = hand.getMeta().getString("weapon");
                Weapon weapon = this.weaponService.fetchWeapon(weaponId);
                Bullet bullet = new Bullet(snowball.getUniqueId(), player, weapon, weapon.getTotalDamage() / weapon.getBullets());
                Bullet.register(bullet);
            }
        }
    }
}
