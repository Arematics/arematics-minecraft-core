package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public SettingsCommand(InventoryService inventoryService) {
        super("settings");
        this.service = inventoryService;
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        return super.onDefaultCLI(sender);
    }

    @Override
    protected boolean onDefaultUI(CorePlayer player) {
        Inventory inv = service.getOrCreate("player.settings", "§6Settings", (byte) 45);
        player.openInventory(inv);
        return true;
    }
}
