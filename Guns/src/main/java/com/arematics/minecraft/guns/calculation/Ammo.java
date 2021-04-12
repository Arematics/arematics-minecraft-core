package com.arematics.minecraft.guns.calculation;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponTypeData;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.data.service.WeaponTypeDataService;
import com.arematics.minecraft.guns.server.Gun;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Ammo {
    private final WeaponService weaponService;
    private final WeaponTypeDataService weaponTypeDataService;
    private final Map<CoreItem, Gun> gunCache = new HashMap<>();
    private final List<CorePlayer> reloading = new ArrayList<>();
    private final List<CorePlayer> aim = new ArrayList<>();
    private final Map<String, Long> shootCooldown = new HashMap<>();

    public List<CorePlayer> getReloading() {
        return reloading;
    }

    public Map<String, Long> getShootCooldown() {
        return shootCooldown;
    }

    public List<CorePlayer> getAim() {
        return aim;
    }

    public Gun fetchGun(CoreItem item){
        if(item.getMeta().hasKey("weapon")){
            String id = item.getMeta().getString("weapon");
            if(gunCache.containsKey(item)) return gunCache.get(item);
            Gun gun = findGunById(item, id);
            gunCache.put(item, gun);
            return gun;
        }
        return null;
    }

    public void loadGun(CorePlayer player, Gun gun){
        if(reloading.contains(player)) return;
        WeaponTypeData data = weaponTypeDataService.findById(gun.getWeapon().getType());
        CoreItem ammo = data.getAmmunition()[0];
        short inInventory = getAmountOfAmmoFromInventory(player, ammo);
        if(inInventory <= 0){
            player.actionBar().sendActionBar("§eNo ammunition in inventory");
        }else{
            player.actionBar().sendActionBar("§c§lLoading Gun...");
            int time = gun.getWeapon().getType().getLoadingSpeed();
            this.reloading.add(player);
            ArematicsExecutor.syncDelayed(() -> {
                CoreItem hand = player.getItemInHand();
                if(hand == null || !hand.isSimilar(gun.getItem())){
                    player.actionBar().sendActionBar("§c§lReload interrupted");
                    this.reloading.remove(player);
                    return;
                }
                short reloaded = gun.reload(inInventory);
                short removed = removeAmmoFromInventory(player, ammo, reloaded);
                player.actionBar().sendActionBar("§a§lReloaded");
                player.getPlayer().getInventory().setItemInHand(gun.getItem());
                player.actionBar().sendActionBar("§a" + removed + " bullets have been reloaded");
                this.reloading.remove(player);
            }, time, TimeUnit.SECONDS);
        }
    }

    public short removeAmmoFromInventory(CorePlayer player, CoreItem ammo, short amount) {
        short removed = 0;
        while(amount > 0){
            int index = player.getPlayer().getInventory().first(ammo.getType());
            if(index == -1) break;
            ItemStack itemStack = player.getPlayer().getInventory().getItem(index);
            if(!ammo.isSimilar(itemStack)) continue;
            if(itemStack.getAmount() <= amount) {
                amount -= itemStack.getAmount();
                removed += itemStack.getAmount();
                player.getPlayer().getInventory().setItem(index, null);
            }else{
                removed += amount;
                itemStack.setAmount(itemStack.getAmount() - amount);
                player.getPlayer().getInventory().setItem(index, itemStack);
                break;
            }
        }
        return removed;
    }

    public short getAmountOfAmmoFromInventory(CorePlayer player, CoreItem ammo){
        return (short) Arrays.stream(player.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(ammo::isSimilar)
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    private Gun findGunById(CoreItem item, String id){
        Weapon weapon = weaponService.fetchWeapon(id);
        return new Gun(weapon, item);
    }



    public void toggleAim(CorePlayer player)
    {
        if (player.hasEffect(PotionEffectType.SLOW)) {
            aim.remove(player);
            player.removePotionEffect(PotionEffectType.SLOW);
        } else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, 4));
            aim.add(player);
        }
    }

    public void stopAim(CorePlayer player)
    {
        if (player.hasEffect(PotionEffectType.SLOW)) {
            aim.remove(player);
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }
}
