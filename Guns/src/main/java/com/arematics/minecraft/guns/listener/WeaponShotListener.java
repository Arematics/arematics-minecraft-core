package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.guns.calculation.Ammo;
import com.arematics.minecraft.guns.server.Gun;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
        CorePlayer player = CorePlayer.get(event.getPlayer());
        CoreItem hand = player.getItemInHand();
        if(hand != null && hand.getMeta().hasKey("weapon") != null){
            String weaponId = hand.getMeta().getString("weapon");
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
                try{
                    Weapon weapon = this.weaponService.fetchWeapon(weaponId);
                    byte bullets = weapon.getBullets();
                    Gun gun = ammo.fetchGun(hand);
                    byte ammunition = gun.getAmmo();
                    if(ammunition < bullets) bullets = ammunition;
                    IntStream.range(0, bullets).forEach((i) -> launchSnowball(weapon.getType(), player));
                    gun.removeAmmo(bullets);
                    player.getPlayer().setItemInHand(gun.getItem());
                    if(bullets == 0)
                        player.warn("No ammunition loaded").handle();
                }catch (RuntimeException ignore){}
            }
        }
    }

    private void launchSnowball(WeaponType weaponType, CorePlayer source){
        int acc = (int) (weaponType.getAccuracy() * 1000);
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
        double rnd = rand.nextDouble();
        vec.multiply(1 + rnd);
        ball.setVelocity(vec);
    }
}
