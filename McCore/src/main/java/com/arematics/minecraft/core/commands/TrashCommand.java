package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@Perm(permission = "trash", description = "open your trashcan")
public class TrashCommand extends CoreCommand {

    @Autowired
    public TrashCommand() {
        super("trash");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Inventory trash = Bukkit.createInventory(null, 27, "§8Trash");
        sender.inventories().openLowerEnabledInventory(trash);
    }

}
