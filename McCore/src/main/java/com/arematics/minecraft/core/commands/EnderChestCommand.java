package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryController;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.global.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Perm(permission = "world.interact.player.enderchest", description = "Permission for watching own enderchest with command")
public class EnderChestCommand extends CoreCommand {

    private final InventoryController controller;

    @Autowired
    public EnderChestCommand(InventoryController inventoryController){
        super("enderchest", "ec");
        this.controller = inventoryController;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.handle(InventoryHandler.class).openLowerEnabledInventory(sender.handle(InventoryHandler.class).getOrCreateInventory("enderchest", "§c"
                + sender.getName() + "'s Enderchest", (byte)36));
    }

    @SubCommand("{target}")
    @Perm(permission = "other", description = "Allows to open enderchest from other players")
    public void openOtherPlayerInventory(CorePlayer player, User target) {
        Optional<CorePlayer> online = target.online(server);
        WrappedInventory ec;
        if(online.isPresent()){
            CorePlayer result = online.get();
            ec = result.handle(InventoryHandler.class).getOrCreateInventory("enderchest", "§c"
                    + result.getName() + "'s Enderchest", (byte)36);
        }
        else ec = controller.findOrCreate(target.getUuid().toString() + ".enderchest", "§c"
                + target.getLastName() + "'s Enderchest", (byte)36);
        boolean edit = player.hasPermission("world.interact.player.enderchest.other.edit");
        if(edit) player.handle(InventoryHandler.class).openLowerEnabledInventory(ec);
        else player.handle(InventoryHandler.class).openTotalBlockedInventory(ec);
    }
}
