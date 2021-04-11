package com.arematics.minecraft.crystals.commands;

import com.arematics.minecraft.animations.firework.FireworkBuilder;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.logic.CrystalKeyItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.ItemCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Perm(permission = "management.keys.existing", description = "Get information about keys")
public class CrystalKeyCommand extends CoreCommand {

    private final CrystalKeyService service;
    private final InventoryService inventoryService;
    private final ItemCollectionService itemCollectionService;

    @Autowired
    public CrystalKeyCommand(CrystalKeyService crystalKeyService,
                             InventoryService inventoryService,
                             ItemCollectionService itemCollectionService) {
        super("crystal-key", true,"ckey", "ck");
        this.service = crystalKeyService;
        this.inventoryService = inventoryService;
        this.itemCollectionService = itemCollectionService;
    }
    
    @SubCommand("{crystal}")
    @Perm(permission = "getNew", description = "permission to get a crystal item")
    public void getCrystalKey(CorePlayer player, CrystalKey key) {
        ArematicsExecutor.syncRun(() -> player.getPlayer().getInventory().addItem(CrystalKeyItem.fromKey(key)));
        player.info("You received a crystal key: " + key.getName()).handle();
    }

    @SubCommand("list")
    public void listCrystals(CorePlayer player) {
        MSG items = MSGBuilder.join(service.findAllNames().stream()
                .map(this::toPart)
                .collect(Collectors.toList()), ',');
        player.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Crystals"))
                .replace("list_value", items)
                .handle();
    }

    private Part toPart(String keyName){
        return new Part(keyName).setHoverAction(HoverAction.SHOW_TEXT, "§7Show info for crystal")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/ck info " + keyName);
    }

    @SubCommand("create {name} {colorCode}")
    @Perm(permission = "modify", description = "Permission to modify a key")
    public void createCrystalKey(CorePlayer player, String name, String colorCode) {
        try{
            service.findById(name);
            player.warn("Crystal with name: " + name + " already exists").handle();
        }catch (RuntimeException re){
            CrystalKey crystalKey = new CrystalKey(name, colorCode.replaceAll("&", "§"));
            service.update(crystalKey);
            player.info("Crystal with name: " + name + " has been created").handle();
        }
    }

    @SubCommand("delete {crystal}")
    @Perm(permission = "modify", description = "Permission to modify a key")
    public void deleteCrystalKey(CorePlayer player, CrystalKey key) {
        try{
            service.delete(key);
            inventoryService.remove("crystal.inventory." + key.getName());
            itemCollectionService.remove(itemCollectionService.findOrCreate("crystal.inventory." + key.getName()));
            player.info("Crystal Key: " + key.getName() + " has been deleted").handle();
        }catch (Exception e){
            player.failure("Crystal Key: " + key.getName() + " could not be deleted").handle();
        }
    }

    @SubCommand("setColorCode {crystal} {colorCode}")
    @Perm(permission = "modify", description = "Permission to modify a key")
    public void changeRGB(CorePlayer player, CrystalKey key, String colorCode) {
        key.setColorCode(colorCode);
        player.info("Color has been changed").handle();
        service.update(key);
    }

    @SubCommand("checkColor {crystal}")
    public void checkCrystalColor(CorePlayer player, CrystalKey key) {
        FireworkBuilder.create().spawn(player.getPlayer(), player.getLocation());
    }

    @SubCommand("info {crystal}")
    public void fetchCrystalInfos(CorePlayer player, CrystalKey key) {
        prettyPrintCrystalInfo(player, key);
    }

    private void prettyPrintCrystalInfo(CorePlayer player, CrystalKey key){
        String builder = "    §7" + "Total Text: " + key.getTotalName() + "\n" +
                "    §7" + "Inventory Key: §c" + "crystal.inventory." + key.getName() + "\n";
        player.info(key.getPrettyPrint())
                .DEFAULT()
                .replace("value", builder)
                .disableServerPrefix()
                .handle();
    }
}
