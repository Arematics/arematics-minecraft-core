package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.player.invsee", description = "See an other players inventory")
public class InvseeCommand extends CoreCommand {
    public InvseeCommand() {
        super("invsee", true);
    }

    @SubCommand("{player}")
    public void seeInventory(CorePlayer player, CorePlayer target) {
        player.check("world.interact.player.invsee.edit")
                .ifPermitted(sender -> player.handle(InventoryHandler.class).openLowerEnabledInventory(target.getPlayer().getInventory()))
                .orElse(sender -> player.handle(InventoryHandler.class).openTotalBlockedInventory(target.getPlayer().getInventory()));
    }
}
