package com.arematics.minecraft.guns.calculation;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponTypeData;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.data.service.WeaponTypeDataService;
import com.arematics.minecraft.guns.server.Gun;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Ammo {
    private final WeaponService weaponService;
    private final WeaponTypeDataService weaponTypeDataService;
    private final Map<String, Gun> gunCache = new HashMap<>();

    public Gun fetchGun(CoreItem item){
        if(item.getMeta().hasKey("weapon")){
            String id = item.getMeta().getString("weapon");
            if(gunCache.containsKey(id)) return gunCache.get(id);
            Gun gun = findGunById(item, id);
            gunCache.put(id, gun);
            return gun;
        }
        return null;
    }

    public void loadGun(CorePlayer player, Gun gun){
        WeaponTypeData data = weaponTypeDataService.findById(gun.getWeapon().getType());
        CoreItem ammo = data.getAmmunition()[0];
        short inInventory = getAmountOfAmmoFromInventory(player, ammo);
        System.out.println("In Inv: " + ammo);
        if(inInventory <= 0){
            player.warn("No ammunition found in inventory, please get ammunition").handle();
        }else{
            byte reloaded = gun.reload(inInventory);
            short removed = removeAmmoFromInventory(player, ammo, reloaded);
            player.getPlayer().getInventory().setItemInHand(gun.getItem());
            player.info("Totally " + removed + " bullets have been reloaded").handle();
        }
    }

    public short removeAmmoFromInventory(CorePlayer player, CoreItem ammo, byte amount) {
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
}
