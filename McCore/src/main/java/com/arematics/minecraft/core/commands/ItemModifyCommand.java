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
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
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
        handleItem(player, item -> item.setName(name).updateTo(player));
        return true;
    }

    @SubCommand("lore set {index} {message}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean setLoreAt(Player player, Integer index, String message) {
        handleItem(player, item -> item.setLoreAt(index, message).updateTo(player));
        return true;
    }

    @SubCommand("lore add {message}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean addToLore(Player player, String message) {
        System.out.println("ÖY");
        handleItem(player, item -> item.addToLore(message).updateTo(player));
        return true;
    }

    @SubCommand("lore rem {index}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean removeFromLore(Player player, Integer index) {
        handleItem(player, item -> item.removeFromLore(index).updateTo(player));
        return true;
    }

    @SubCommand("lore clear")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean clearLore(Player player) {
        handleItem(player, item -> item.clearLore().updateTo(player));
        return true;
    }

    @SubCommand("bindCommand {message}")
    @Perm(permission = "bind_command", description = "Modify Item interact command")
    public boolean bindCommandToItem(Player player, String message) {
        handleItem(player, item -> item.bindCommand(message).updateTo(player));
        return true;
    }

    @SubCommand("bindMeta {key} {value}")
    @Perm(permission = "bind_meta", description = "Modify Item Metadata")
    public boolean bindMeta(Player player, String key, String value) {
        handleItem(player, item -> item.setString(key, value).updateTo(player));
        return true;
    }

    private void handleItem(Player player, Consumer<CoreItem> consumer){
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) consumer.accept(itemStack);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();  
    }
}
