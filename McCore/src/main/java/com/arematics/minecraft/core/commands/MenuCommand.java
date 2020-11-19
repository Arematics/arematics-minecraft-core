package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public MenuCommand(InventoryService inventoryService) {
        super("menu");
        this.service = inventoryService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        onUI(sender);
    }

    private boolean onUI(CommandSender sender){
        Player player = (Player) sender;
        Inventory inv = service.getOrCreate("player-menu", "§6Menu", (byte) 54);
        player.openInventory(inv);
        return true;
    }
}
