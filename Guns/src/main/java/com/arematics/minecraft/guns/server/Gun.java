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

    public byte getTotalDamage() {
        return weapon.getTotalDamage();
    }

    public byte getBullets() {
        return weapon.getBullets();
    }

    public short getDurability() {
        return weapon.getDurability();
    }

    public short getMaxAmmo() {
        return weapon.getMaxAmmo();
    }

    public byte getAmmoPerLoading(){
        return weapon.getAmmoPerLoading();
    }

    public byte getAmmo(){
        return this.item.getMeta().getByte("ammo");
    }

    public void removeAmmo(){
        byte current = this.item.getMeta().getByte("ammo");
        updateAmmo((byte) (current - this.getBullets()));
    }

    public void removeAmmo(byte amount){
        byte current = this.item.getMeta().getByte("ammo");
        updateAmmo((byte) (current - amount));
    }

    public byte reload(short ammoInInventory){
        byte current = this.item.getMeta().getByte("ammo");
        if(current >= getAmmoPerLoading()) return 0;
        byte free = (byte) (getAmmoPerLoading() - current);
        if(free >= ammoInInventory){
            updateAmmo((byte) (current + ammoInInventory));
            return (byte) ammoInInventory;
        }else{
            updateAmmo((byte) (current + free));
            return free;
        }
    }

    private void updateAmmo(byte newAmount){
        this.item.getMeta().setByte("ammo", newAmount);
        this.item.setName("§e" + this.getId() + " §7< " + this.item.getMeta().getByte("ammo") + " / " + this.getAmmoPerLoading() + " >");
    }
}
