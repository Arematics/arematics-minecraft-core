package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.events.PlayerInteractEvent;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.InteractType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.guns.calculation.Ammo;
import com.arematics.minecraft.guns.server.Gun;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class WeaponShotListener implements Listener {

    private final Ammo ammo;
    private final WeaponService weaponService;

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CorePlayer player = event.getPlayer();
        CoreItem hand = player.getItemInHand();
        if(hand != null && hand.getMeta().hasKey("weapon") != null){
            if(player.regions().inRegionWithFlag(DefaultFlag.PVP)) return;
            String weaponId = hand.getMeta().getString("weapon");
            if(ammo.getReloading().contains(player)){
                player.getActionBar().sendActionBar("§c§lWait until reload end");
                return;
            }
            String cooldownKey = player.getUUID().toString() + weaponId;
            if(ammo.getShootCooldown().containsKey(cooldownKey) &&
                    ammo.getShootCooldown().get(cooldownKey) > System.currentTimeMillis()) return;
            if(event.getType() == InteractType.RIGHT_CLICK){
                try{
                    Weapon weapon = this.weaponService.fetchWeapon(weaponId);
                    short bullets = weapon.getBullets();
                    Gun gun = ammo.fetchGun(hand);
                    short ammunition = gun.getAmmo();
                    if(ammunition != 0) {
                        IntStream.range(0, bullets).forEach((i) -> launchSnowball(weapon.getType(), player));
                        gun.removeAmmo((short) 1);
                        player.getPlayer().setItemInHand(gun.getItem());
                        ammo.getShootCooldown().put(cooldownKey, System.currentTimeMillis() + (long) (1000 * weapon.getType().getShootSpeed()));
                    }else
                        player.getActionBar().sendActionBar("§eNo ammunition loaded");
                }catch (RuntimeException ignore){}
            }
        }
    }

    private void launchSnowball(WeaponType weaponType, CorePlayer source){
        int acc = (int) (weaponType.getAccuracy() * 1000);
        if(weaponType == WeaponType.SNIPER && ammo.getAim().contains(source)) acc = 1;
        Snowball ball = source.getPlayer().launchProjectile(Snowball.class);
        /*Item item = source.getPlayer().getWorld().dropItem(source.getLocation(), CoreItem.generate(Material.REDSTONE_BLOCK));
        item.setPickupDelay(999999);

        ball.setPassenger(item);*/

        Random rand = new Random();
        Location ploc = source.getLocation();
        double dir = -ploc.getYaw() - 90.0F;
        double pitch = -ploc.getPitch();
        double xwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
        double ywep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
        double zwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
        double xd = Math.cos(Math.toRadians(dir)) *
                Math.cos(Math.toRadians(pitch)) + xwep;
        double yd = Math.sin(Math.toRadians(pitch)) + ywep;
        double zd = -Math.sin(Math.toRadians(dir)) *
                Math.cos(Math.toRadians(pitch)) + zwep;
        Vector vec = new Vector(xd, yd, zd);
        vec.multiply(weaponType.getSpeed());
        ball.setVelocity(vec);
    }
}
