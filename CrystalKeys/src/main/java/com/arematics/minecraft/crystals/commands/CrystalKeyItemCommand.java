package com.arematics.minecraft.crystals.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.crystals.logic.CrystalMetaParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "crystals.manage.item", description = "Permission to modify item for crystal keys")
public class CrystalKeyItemCommand extends CoreCommand {

    private final CrystalMetaParser parser;

    @Autowired
    public CrystalKeyItemCommand(CrystalMetaParser crystalMetaParser){
        super("cki", "crystalitem");
        this.parser = crystalMetaParser;
    }
    
    @SubCommand("setChance {chance}")
    public void setCrystalKeyChance(CorePlayer player, Double chance) {
        CoreItem hand = player.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        String rarity = fetchRarityByPercent(chance);
        hand.setString("chance", String.valueOf(chance))
                .setOrAddLoreAt(0, rarity + " §8[§b" + chance + "%§8]");
        player.setItemInHand(hand);
        player.info("Crystal Item Chance set").handle();
    }

    @SubCommand("list metas")
    public void listCrystalItemMeta(CorePlayer sender) {
        sender.info("listing")
                .DEFAULT()
                .replace("list_type", "Crystal Meta")
                .replace("list_value", "§c" + StringUtils.join(parser.getTypes().keySet(), ", "))
                .handle();
    }

    @SubCommand("set meta {key} {value}")
    public void setCrystalItemMeta(CorePlayer sender, String key, String value) {
        if(!parser.getTypes().containsKey(key))
            throw new CommandProcessException("Not a valid crystal item meta key");
        CoreItem hand = sender.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        hand.setString(key, value);
        sender.setItemInHand(hand);
        sender.info("Crystal Item Meta set").handle();
    }

    @SubCommand("check")
    public void checkItemForCrystal(CorePlayer sender) {
        CoreItem hand = sender.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        String rawChance = hand.getMeta().getString("chance");
        try{
            double chance = Double.parseDouble(rawChance);
            if(chance < 0.01 || chance > 100) throw new CommandProcessException("Chance is out of range");
        }catch (Exception e){
            throw new CommandProcessException("Item chance is not correct");
        }
    }

    private String fetchRarityByPercent(double chance){
        if(chance >= 8) return "§7Common";
        if(chance >= 5) return "§9Rare";
        if(chance >= 3) return "§6Super Rare";
        if(chance >= 1) return "§5Ultra Rare";
        return "§4Secret Rare";
    }
}
