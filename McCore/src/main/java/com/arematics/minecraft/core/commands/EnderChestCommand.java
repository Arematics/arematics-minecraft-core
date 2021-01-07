package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)) throw new CommandProcessException("Only Players could perform this command");
        CorePlayer player = CorePlayer.get((Player) sender);
        player.openLowerEnabledInventory(player.getOrCreateInventory("enderchest", "§c"
                + player.getPlayer().getName() + "'s Enderchest", (byte)36));
    }

    @SubCommand("{target}")
    @Perm(permission = "other", description = "Allows to open enderchest from other players")
    public void openOtherPlayerInventory(CorePlayer player, User target) {
        Inventory ec = service.getOrCreate(target.getUuid().toString() + ".enderchest", "§c"
                + target.getLastName() + "'s Enderchest", (byte)36);
        boolean edit = player.hasPermission("player.inventory.enderchest.other.edit");
        if(edit) openEnabledInventory(player, ec);
        else openBlocked(player, ec);
    }

    private void openEnabledInventory(CorePlayer player, Inventory inventory){
        player.openLowerEnabledInventory(inventory);
    }

    private void openBlocked(CorePlayer player, Inventory inventory){
        player.openTotalBlockedInventory(inventory);
    }
}
