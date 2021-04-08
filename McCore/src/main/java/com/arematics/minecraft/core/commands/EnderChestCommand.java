package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "player.inventory.enderchest", description = "Permission for watching own enderchest with command")
public class EnderChestCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public EnderChestCommand(InventoryService service){
        super("enderchest", "ec");
        this.service = service;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.inventories().openLowerEnabledInventory(sender.inventories().getOrCreateInventory("enderchest", "§c"
                + sender.getName() + "'s Enderchest", (byte)36));
    }

    @SubCommand("{target}")
    @Perm(permission = "other", description = "Allows to open enderchest from other players")
    public void openOtherPlayerInventory(CorePlayer player, User target) {
        CorePlayer online = target.online();
        WrappedInventory ec;
        if(online != null) ec = online.inventories().getOrCreateInventory("enderchest", "§c"
                + online.getName() + "'s Enderchest", (byte)36);
        else ec = service.findOrCreate(target.getUuid().toString() + ".enderchest", "§c"
                + target.getLastName() + "'s Enderchest", (byte)36);
        boolean edit = player.hasPermission("player.inventory.enderchest.other.edit");
        if(edit) player.inventories().openLowerEnabledInventory(ec);
        else player.inventories().openTotalBlockedInventory(ec);
    }
}
