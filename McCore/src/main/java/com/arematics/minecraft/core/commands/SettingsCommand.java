package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public SettingsCommand(InventoryService inventoryService) {
        super("settings", true);
        this.service = inventoryService;
    }

    @Override
    protected void onDefaultGUI(CorePlayer player) {
        WrappedInventory inv = service.findOrCreateGlobal("player.settings", "§6Settings", (byte) 45);
        player.inventories().openInventory(inv);
    }
}
