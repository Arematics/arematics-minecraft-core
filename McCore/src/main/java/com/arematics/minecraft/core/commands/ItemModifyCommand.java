package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PluginCommand(aliases = {})
@Perm(permission = "itemmodifier", description = "Allows full permission to item modification command")
public class ItemModifyCommand extends CoreCommand {

    public ItemModifyCommand(){
        super("imodify");
        registerLongArgument("lore");
        registerLongArgument("command");
    }

    @Default
    @Override
    public boolean onDefaultExecute(CommandSender sender){
        List<String> subCommands = super.getSubCommands();
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", subCommands.toArray(new String[]{}))
                .setHover(HoverAction.SHOW_TEXT, "Open to chat")
                .setClick(ClickAction.SUGGEST_COMMAND, "/imodify %value%")
                .END()
                .handle();
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

    @SubCommand("lore set {index} {lore}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean setLoreAt(Player player, Integer index, String lore) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.setLoreAt(index, lore).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("lore add {lore}")
    @Perm(permission = "lore", description = "Modify Item Lore")
    public boolean addToLore(Player player, String lore) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.addToLore(lore).updateTo(player);
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

    @SubCommand("bindCommand {command}")
    @Perm(permission = "bind_command", description = "Modify Item interact command")
    public boolean bindCommandToItem(Player player, String command) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.bindCommand(command).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }

    @SubCommand("bindMeta {key} {value}")
    @Perm(permission = "bind_meta", description = "Modify Item Metadata")
    public boolean bindMeta(Player player, String key, String value) {
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) itemStack.setString(key, value).updateTo(player);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
        return true;
    }
}
