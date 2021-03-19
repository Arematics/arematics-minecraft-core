package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.items.parser.ItemMetaParser;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "itemmodifier", description = "Allows full permission to item modification command")
public class ItemModifyCommand extends CoreCommand {

    private final ItemMetaParser itemMetaParser;

    @Autowired
    public ItemModifyCommand(ItemMetaParser itemMetaParser){
        super("imodify", "im", "itemmod");
        registerLongArgument("lore");
        registerLongArgument("command");
        this.itemMetaParser = itemMetaParser;
    }

    @SubCommand("name {name}")
    @Perm(permission = "name", description = "Modify Item Name")
    public boolean changeItemName(CorePlayer player, String name) {
        CoreItem.executeOnHandItem(player, item -> item.setName(name).updateTo(player));
        return true;
    }

    @SubCommand("lore set {index} {lore}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean setLoreAt(CorePlayer player, Integer index, String lore) {
        CoreItem.executeOnHandItem(player, item -> item.setLoreAt(index, lore).updateTo(player));
        return true;
    }

    @SubCommand("lore add {lore}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean addToLore(CorePlayer player, String lore) {
        CoreItem.executeOnHandItem(player, item -> item.addToLore(lore).updateTo(player));
        return true;
    }

    @SubCommand("lore rem {index}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean removeFromLore(CorePlayer player, Integer index) {
        CoreItem.executeOnHandItem(player, item -> item.removeFromLore(index).updateTo(player));
        return true;
    }

    @SubCommand("lore clear")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean clearLore(CorePlayer player) {
        CoreItem.executeOnHandItem(player, item -> item.clearLore().updateTo(player));
        return true;
    }

    @SubCommand("bindCommand {command}")
    @Perm(permission = "meta_interaction", description = "Modify Item Meta")
    public boolean bindCommandToItem(CorePlayer player, String command){
        CoreItem.executeOnHandItem(player, item -> item.bindCommand(command).updateTo(player));
        return true;
    }

    @SubCommand("readMeta {key}")
    @Perm(permission = "meta_interaction", description = "Modify Item Meta")
    public boolean readMeta(CorePlayer player, String key){
        CoreItem.executeOnHandItem(player, item -> player.info("item_meta_key_value")
                .DEFAULT()
                .replace("key", key)
                .replace("value", item.readMetaValue(key))
                .handle());
        return true;
    }

    @SubCommand("bindMeta {key} {value}")
    @Perm(permission = "meta_interaction", description = "Modify Item Meta")
    public boolean bindMeta(CorePlayer player, String key, String value) {
        CoreItem.executeOnHandItem(player, item -> item.setString(key, value).updateTo(player));
        return true;
    }

    @SubCommand("delMeta {key}")
    @Perm(permission = "meta_interaction", description = "Modify Item Meta")
    public boolean delMeta(CorePlayer player, String key) {
        CoreItem.executeOnHandItem(player, item -> item.removeString(key).updateTo(player));
        return true;
    }

    @SubCommand("items get back")
    public void getBackItem(CorePlayer sender) {
        sender.getPlayer().getInventory().addItem(CoreItem.create(Items.BACK.clone()));
        sender.info("Received back item").handle();
    }



    @SubCommand("list metas")
    public void listCrystalItemMeta(CorePlayer sender) {
        sender.info("listing")
                .DEFAULT()
                .replace("list_type", "Item Meta")
                .replace("list_value", "§c" + StringUtils.join(itemMetaParser.getTypes().keySet(), ", "))
                .handle();
    }

    @SubCommand("set meta {key} {value}")
    public void setCrystalItemMeta(CorePlayer sender, String key, String value) {
        if(!itemMetaParser.getTypes().containsKey(key))
            throw new CommandProcessException("command_item_modify_key_not_valid");
        CoreItem hand = sender.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        hand.setString(key, value);
        sender.setItemInHand(hand);
        sender.info("command_item_modify_meta_set").handle();
    }
}
