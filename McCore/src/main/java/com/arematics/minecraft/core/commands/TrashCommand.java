package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "trash", description = "open your trashcan")
public class TrashCommand extends CoreCommand {

    @Autowired
    public TrashCommand() {
        super("trash");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {

        Inventory trash = Bukkit.createInventory(((Player) sender).getPlayer(), 27, "§8Trash");
        ((Player) sender).openInventory(trash);
    }

}
