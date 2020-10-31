package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PluginCommand(aliases = {})
@Perm(permission = "inventory-manager")
public class InventoryManagerCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public InventoryManagerCommand(InventoryService inventoryService){
        super("inv-manager");
        this.service = inventoryService;
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
                .setClick(ClickAction.SUGGEST_COMMAND, "/inv-manager %value%")
                .END()
                .handle();
        return true;
    }

    @SubCommand("checkAmboss {value}")
    public boolean ambossInputCheck(Player player, String value) throws InterruptedException {
        player.sendMessage(value);
        return true;
    }

    @SubCommand("modify {key}")
    public boolean modifyInventoryContent(Player player, String key) {
        try{
            player.openInventory(service.getInventory(key));
        }catch (RuntimeException exception){
            Messages.create("Non inventory found").WARNING().to(player).handle();
        }
        return true;
    }

    @SubCommand("keys {by}")
    public boolean getKeysBy(Player player, String by) {
        List<String> keys = service.findKeys(by);
        Messages.create("cmd_not_valid")
                .to(player)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", keys.toArray(new String[]{}))
                .setHover(HoverAction.SHOW_TEXT, "Edit Inventory %value%")
                .setClick(ClickAction.RUN_COMMAND, "/inv-manager modify %value%")
                .END()
                .handle();
        return true;
    }
}
