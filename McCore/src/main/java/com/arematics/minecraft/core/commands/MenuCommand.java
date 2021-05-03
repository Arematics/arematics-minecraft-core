package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryController;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuCommand extends CoreCommand {

    private final InventoryController inventoryController;

    @Autowired
    public MenuCommand(InventoryController inventoryController) {
        super("menu");
        this.inventoryController = inventoryController;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        WrappedInventory inv = inventoryController.findOrCreate("player-menu", "§6Menu", (byte) 54);
        sender.handle(InventoryHandler.class).openTotalBlockedInventory(inv);
    }
}
