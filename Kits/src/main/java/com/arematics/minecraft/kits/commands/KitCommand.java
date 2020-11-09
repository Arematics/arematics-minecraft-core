package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.KitService;
import com.arematics.minecraft.data.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class KitCommand extends CoreCommand {

    private final KitService service;
    private final UserService userService;
    private final InventoryService inventoryService;

    @Autowired
    public KitCommand(KitService kitService, UserService userService, InventoryService inventoryService) {
        super("kit", "kits");
        this.service = kitService;
        this.userService = userService;
        this.inventoryService = inventoryService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        String msg = "§a\n\n§7Kits" + " » " + "§c%kits%";
        String joined = StringUtils.join(service.findKitNames(), ", ");
        Messages.create(msg)
                .to(sender)
                .DEFAULT()
                .replace("kits", StringUtils.isBlank(joined) ? "-" : joined)
                .disableServerPrefix()
                .handle();
    }

    @SubCommand("{kit}")
    public boolean giveKit(Player player, Kit kit) {
        giveToPlayer(kit, player, true);
        return true;
    }

    private void giveToPlayer(Kit kit, Player player, boolean force){
        if(!force && !service.isPermitted(player.getUniqueId(), kit)){
            Messages.create("cmd_noperms")
                    .WARNING()
                    .to(player)
                    .handle();
            return;
        }
        Inventory inv = inventoryService.getOrCreate("kit.inventory." + kit.getName(), "§6Kit " + kit.getName(),
                (byte)27);
        ItemStack[] items = Arrays.stream(inv.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .toArray(ItemStack[]::new);
        player.getInventory().addItem(items);
    }
}
