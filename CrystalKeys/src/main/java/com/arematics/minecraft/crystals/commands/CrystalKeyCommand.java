package com.arematics.minecraft.crystals.commands;

import com.arematics.minecraft.animations.firework.FireworkBuilder;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.logic.CrystalKeyItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import org.bukkit.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Perm(permission = "crystal-key", description = "Permission to get a crystal key")
public class CrystalKeyCommand extends CoreCommand {

    private final CrystalKeyService service;

    @Autowired
    public CrystalKeyCommand(CrystalKeyService crystalKeyService) {
        super("crystal-key", "ckey", "ck");
        this.service = crystalKeyService;
    }
    
    @SubCommand("{crystal}")
    @Perm(permission = "item", description = "permission to get a crystal item")
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

    @SubCommand("create {name} {colorCode} {r} {g} {b}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void createCrystalKey(CorePlayer player, String name, String colorCode, Byte r, Byte g, Byte b) {
        try{
            service.findById(name);
            player.warn("Crystal with name: " + name + " already exists").handle();
        }catch (RuntimeException re){
            CrystalKey crystalKey = new CrystalKey(name, colorCode.replaceAll("&", "§"), Color.fromBGR(r, g, b));
            service.update(crystalKey);
            player.info("Crystal with name: " + name + " has been created").handle();
        }
    }

    @SubCommand("delete {crystal}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void deleteCrystalKey(CorePlayer player, CrystalKey key) {
        try{
            service.delete(key);
            player.info("Crystal Key: " + key.getName() + " has been deleted").handle();
        }catch (Exception e){
            player.failure("Crystal Key: " + key.getName() + " could not be deleted").handle();
        }
    }

    @SubCommand("setRGB {crystal} {r} {g} {b}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void changeRGB(CorePlayer player, CrystalKey key, Byte r, Byte g, Byte b) {
        key.setColor(Color.fromBGR(r, g, b));
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
        String msg = "§a\n\n§7Crystal Key" + " » " + "§c" + key.getName() + "\n" +
                "%information%";
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("     §7" + "RGB: " + " §c" +
                key.getColor().getRed() + "R " + key.getColor().getGreen() + "G " + key.getColor().getBlue() + "B" + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change rgb color")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/ck setRGB " + key.getName() + " {r} {g} {b}"));
        player.info(msg)
                .setInjector(AdvancedMessageInjector.class)
                .replace("information", new MSG(parts))
                .disableServerPrefix()
                .handle();
    }
}
