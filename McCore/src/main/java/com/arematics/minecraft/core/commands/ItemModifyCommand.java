package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@PluginCommand(aliases = {})
@Perm(permission = "itemmodifier", description = "Allows full permission to item modification command")
public class ItemModifyCommand extends CoreCommand {

    public ItemModifyCommand(){
        super("imodify");
    }
    
    @Default
    public boolean defaultInformation(CommandSender sender){
        return true;
    }

    @SubCommand("name {name}")
    @Perm(permission = "name", description = "Modify Item Name")
    public boolean changeItemName(Player player, String name) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.setName(name).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("lore set {index} {message}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean setLoreAt(Player player, Integer index, String message) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.setLoreAt(index, message).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("lore add {message}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean addToLore(Player player, String message) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.addToLore(message).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("lore rem {index}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean removeFromLore(Player player, Integer index) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.removeFromLore(index).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("lore clear")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean clearLore(Player player) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.clearLore().updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }
}
