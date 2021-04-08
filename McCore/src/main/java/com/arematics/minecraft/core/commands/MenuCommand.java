package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.service.InventoryService;
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
    public void onDefaultExecute(CorePlayer sender) {
        WrappedInventory inv = service.findOrCreate("player-menu", "§6Menu", (byte) 54);
        sender.inventories().openTotalBlockedInventory(inv);
    }
}
