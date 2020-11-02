package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Perm(permission = "inventory-manager")
public class InventoryManagerCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public InventoryManagerCommand(InventoryService inventoryService){
        super("inv-manager");
        this.service = inventoryService;
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
        Part[] items = keys.stream().map(this::toInventoryModifierPart).toArray(Part[]::new);
        Messages.create("cmd_not_valid")
                .to(player)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", items)
                .handle();
        return true;
    }

    private Part toInventoryModifierPart(String key){
        return new Part(key)
                .setHoverAction(HoverAction.SHOW_TEXT, "Edit Inventory " + key)
                .setClickAction(ClickAction.RUN_COMMAND, "/inv-manager modify " + key);
    }
}
