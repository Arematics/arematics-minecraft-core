package com.arematics.minecraft.guns.commands;

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
        player.info("Display Item for type: " + type.name() + " has been changed").handle();
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
        int slot = 2;
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
        List<Weapon> weapons = this.weaponService.findAllByType(type);
        int size = 18 + ((weapons.size() / 9) * 9);
        Inventory inv = Bukkit.createInventory(null, size, "§cWeapons");
        player.openTotalBlockedInventory(inv);
        weapons.forEach(weapon -> inv.addItem(prepareWeapon(weapon)));
        inv.setItem(size - 1, createNewWeapon(type));
    }

    @SubCommand("create weapon {id} {type} {durability} {damage} {bullets}")
    public void createNewWeapon(CorePlayer player, String id, WeaponType type, Short durability, Byte damage, Byte bullets) {
        CoreItem hand = player.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        try{
            this.weaponService.fetchWeapon(id);
            player.warn("Weapon with id: " + id + " already exists").handle();
        }catch (RuntimeException re){
            Weapon weapon = new Weapon(id, type, damage, bullets, durability, new CoreItem[]{CoreItem.create(hand)
                    .setString("weapon", id)
                    .setShort("weapon-durability", durability)
                    .setName("§e" + id)});
            this.weaponService.update(weapon);
            player.info("Weapon with id: " + id + " has been created").handle();
        }
    }

    @SubCommand("get weapon {id}")
    public void getWeapon(CorePlayer player, String id) {
        try{
            Weapon weapon = this.weaponService.fetchWeapon(id);
            player.getPlayer().getInventory().addItem(weapon.getWeaponItem());
            player.info("Recieved weapon: " + id).handle();
        }catch (RuntimeException re){
            player.warn("Weapon with id: " + id + " not exists").handle();
        }
    }

    private CoreItem createNewWeapon(WeaponType weaponType){
        return CoreItem.generate(Material.EMERALD)
                .bindCommand("wadmin create weapon {id} " + weaponType.name() + " {durability} {damage} {bullets}")
                .setName("§aCreate new Weapon")
                .addToLore("    §8> Click to create new weapon for this type")
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
