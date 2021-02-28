package com.arematics.minecraft.guns.server;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Gun {

    private final Weapon weapon;
    private final CoreItem item;

    public String getId() {
        return weapon.getId();
    }

    public WeaponType getType() {
        return weapon.getType();
    }

    public short getTotalDamage() {
        return weapon.getTotalDamage();
    }

    public short getBullets() {
        return weapon.getBullets();
    }

    public short getDurability() {
        return weapon.getDurability();
    }

    public short getMaxAmmo() {
        return weapon.getMaxAmmo();
    }

    public short getAmmoPerLoading(){
        return weapon.getAmmoPerLoading();
    }

    public short getAmmo(){
        return this.item.getMeta().getShort("ammo");
    }

    public void removeAmmo(){
        short current = this.item.getMeta().getShort("ammo");
        updateAmmo((short) (current - this.getBullets()));
    }

    public void removeAmmo(short amount){
        short current = this.item.getMeta().getShort("ammo");
        updateAmmo((short) (current - amount));
    }

    public short reload(short ammoInInventory){
        short current = this.item.getMeta().getShort("ammo");
        if(current >= getAmmoPerLoading()) return 0;
        short free = (short) (getAmmoPerLoading() - current);
        if(free >= ammoInInventory){
            updateAmmo((short) (current + ammoInInventory));
            return ammoInInventory;
        }else{
            updateAmmo((short) (current + free));
            return free;
        }
    }

    private void updateAmmo(short newAmount){
        this.item.getMeta().setShort("ammo", newAmount);
        this.item.setName("§e" + this.getId() + " §7< " + this.item.getMeta().getShort("ammo") + " / " + this.getAmmoPerLoading() + " >");
    }
}
