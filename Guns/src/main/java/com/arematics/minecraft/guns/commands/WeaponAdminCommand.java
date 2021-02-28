package com.arematics.minecraft.guns.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.mode.model.WeaponTypeData;
import com.arematics.minecraft.data.service.WeaponService;
import com.arematics.minecraft.data.service.WeaponTypeDataService;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Perm(permission = "guns.weapons.administrate")
public class WeaponAdminCommand extends CoreCommand {

    private final WeaponService weaponService;
    private final WeaponTypeDataService weaponTypeDataService;

    @Autowired
    public WeaponAdminCommand(WeaponService weaponService,
                              WeaponTypeDataService weaponTypeDataService){
        super("wadmin", "weapona");
        this.weaponService = weaponService;
        this.weaponTypeDataService = weaponTypeDataService;
    }

    @SubCommand("setDisplayItem {type}")
    public void setWeaponTypeDisplay(CorePlayer player, WeaponType type) {
        CoreItem hand = player.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        WeaponTypeData data;
        try{
            data = this.weaponTypeDataService.findById(type);
        }catch (RuntimeException re){
            data = new WeaponTypeData(type, new CoreItem[]{}, new CoreItem[]{});
        }
        data.setTypeItem(new CoreItem[]{hand});
        this.weaponTypeDataService.update(data);
        player.info("Display Item for type: " + type.name() + " has been changed").handle();
    }

    @SubCommand("setAmmunition {type}")
    public void setWeaponAmmunition(CorePlayer player, WeaponType type) {
        CoreItem hand = player.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        WeaponTypeData data;
        try{
            data = this.weaponTypeDataService.findById(type);
        }catch (RuntimeException re){
            data = new WeaponTypeData(type, new CoreItem[]{}, new CoreItem[]{hand});
        }
        data.setAmmunition(new CoreItem[]{hand});
        this.weaponTypeDataService.update(data);
        player.info("Ammunition Item for type: " + type.name() + " has been changed").handle();
    }

    @SubCommand("list types")
    public void listWeaponTypes(CorePlayer player) {
        player.info("listing")
                .DEFAULT()
                .replace("list_type", "Weapon Types")
                .replace("list_value", "§c" + StringUtils.join(WeaponType.values(), ", "))
        .handle();
    }

    @SubCommand("config")
    public void configureWeapons(CorePlayer player) {
        Inventory inv = Bukkit.createInventory(null, 9, "§cWeapon Types");
        int slot = 1;
        for(WeaponType weaponType : WeaponType.values()){
            try{
                WeaponTypeData data = this.weaponTypeDataService.findById(weaponType);
                CoreItem display = data.getTypeItem()[0];
                display.bindCommand("wadmin config " + weaponType.name())
                        .setName("§7Weapons for type: §e" + weaponType.name())
                        .addToLore("    §7> Click to see weapons for this type");
                inv.setItem(slot, display);
                slot += 2;
            }catch (RuntimeException re){
                throw new CommandProcessException("Weapon Types configuration not completed yet");
            }
        }
        player.openTotalBlockedInventory(inv);
    }

    @SubCommand("config {type}")
    public void configureWeapons(CorePlayer player, WeaponType type) {
        try{
            List<Weapon> weapons = this.weaponService.findAllByType(type);
            int size = 18 + ((weapons.size() / 9) * 9);
            Inventory inv = Bukkit.createInventory(null, size, "§cWeapons");
            player.openTotalBlockedInventory(inv);
            weapons.forEach(weapon -> inv.addItem(prepareWeapon(weapon)));
            inv.setItem(size - 1, createNewWeapon(type));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SubCommand("create weapon {id} {type} {durability} {damage} {bullets} {apl}")
    public void createNewWeapon(CorePlayer player, String id,
                                WeaponType type, Short durability,
                                Short damage, Short bullets,
                                Short ammoPerLoading) {
        CoreItem hand = player.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        try{
            this.weaponService.fetchWeapon(id);
            player.warn("Weapon with id: " + id + " already exists").handle();
        }catch (RuntimeException re){
            Weapon weapon = new Weapon(id, type, damage, bullets, durability, (short) 0, ammoPerLoading,
                    new CoreItem[]{CoreItem.create(hand)
                    .setString("weapon", id)
                    .setShort("weapon-durability", durability)
                    .setName("§e" + id + " §7< 0 / " + ammoPerLoading + " >")});
            this.weaponService.update(weapon);
            player.info("Weapon with id: " + id + " has been created").handle();
        }
    }

    @SubCommand("set durability {id} {durability}")
    public void setDurability(CorePlayer sender, String id, Short durability) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            weapon.setDurability(durability);
            weaponService.update(weapon);
            sender.info("Weapon durability for weapon: " + id + " changed to: " + durability).handle();
        }catch (RuntimeException re){
            throw new CommandProcessException("Weapon with name: " + id + " could not be found");
        }
    }

    @SubCommand("set damage {id} {totalDamage}")
    public void setDamage(CorePlayer sender, String id, Short damage) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            weapon.setTotalDamage(damage);
            weaponService.update(weapon);
            sender.info("Weapon totalDamage for weapon: " + id + " changed to: " + damage).handle();
        }catch (RuntimeException re){
            throw new CommandProcessException("Weapon with name: " + id + " could not be found");
        }
    }

    @SubCommand("set bullets {id} {bullets}")
    public void setBullets(CorePlayer sender, String id, Short bullets) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            weapon.setBullets(bullets);
            weaponService.update(weapon);
            sender.info("Weapon bullets for weapon: " + id + " changed to: " + bullets).handle();
        }catch (RuntimeException re){
            throw new CommandProcessException("Weapon with name: " + id + " could not be found");
        }
    }

    @SubCommand("set apl {id} {apl}")
    public void setAmmoPerLoading(CorePlayer sender, String id, Short apl) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            weapon.setAmmoPerLoading(apl);
            weaponService.update(weapon);
            sender.info("Weapon ammo per loading for weapon: " + id + " changed to: " + apl).handle();
        }catch (RuntimeException re){
            throw new CommandProcessException("Weapon with name: " + id + " could not be found");
        }
    }

    @SubCommand("get weapon {id}")
    public void getWeapon(CorePlayer player, String id) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            CoreItem item = weapon.getWeaponItem()[0].setByte("ammo", (byte) 0);
            player.getPlayer().getInventory().addItem(item);
            player.info("Recieved weapon: " + id).handle();
        }catch (RuntimeException re){
            player.warn("Weapon with id: " + id + " not exists").handle();
        }
    }

    private CoreItem createNewWeapon(WeaponType weaponType){
        return CoreItem.generate(Material.EMERALD)
                .bindCommand("wadmin create weapon {id} " + weaponType.name() + " {durability} {damage} {bullets} {apl}")
                .setName("§aCreate new Weapon")
                .addToLore("    §8> Click to create new weapon for this type")
                .addToLore("    §8> apl means Ammo per Loading (Max Ammo per Loading Cycle)")
                .addToLore("    §8> Item in your hand should be the wanted weapon item");
    }

    private CoreItem prepareWeapon(Weapon weapon){
        return CoreItem.create(weapon.getWeaponItem()[0])
                .bindCommand("wadmin get weapon " + weapon.getId())
                .setName("§8Weapon: §e" + weapon.getId())
                .addToLore("    §8> Total Damage: §e" + weapon.getTotalDamage())
                .addToLore("    §8> Bullets: §e" + weapon.getBullets())
                .addToLore("    §8> Damage per Bullet: §e" + (weapon.getTotalDamage() / weapon.getBullets()))
                .addToLore("    §c> Click to get weapon");
    }
}
