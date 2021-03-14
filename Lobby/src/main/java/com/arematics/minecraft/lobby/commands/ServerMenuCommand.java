package com.arematics.minecraft.lobby.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.springframework.stereotype.Component;

@Component
public class ServerMenuCommand extends CoreCommand {

    public ServerMenuCommand(){
        super("servermenu");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Inventory inv = Bukkit.createInventory(null, 3*9, "§bSelect Server");
        sender.inventories().openTotalBlockedInventory(inv);
        InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
        inv.setItem(9 + 3, CoreItem.generate(Material.DIAMOND_SWORD)
                .bindCommand("servermenu connect pvp")
                .setName("§bPVP"));
        inv.setItem(9 + 5, CoreItem.generate(Material.GOLD_AXE)
                .bindCommand("servermenu connect guns")
                .setName("§6GUNS"));
    }

    @SubCommand("connect {server}")
    public void connectToServer(CorePlayer player, String server) {
        MessagingUtils.sendToServer(player, server);
    }
}
