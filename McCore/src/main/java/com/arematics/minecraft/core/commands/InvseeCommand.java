package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "invsee", description = "See an other players inventory")
public class InvseeCommand extends CoreCommand {
    public InvseeCommand() {
        super("invsee", true);
    }

    @SubCommand("{player}")
    public void seeInventory(CorePlayer player, CorePlayer target) {
        player.check("invsee-change")
                .ifPermitted(sender -> player.inventories().openLowerEnabledInventory(target.getPlayer().getInventory()))
                .orElse(sender -> player.inventories().openTotalBlockedInventory(target.getPlayer().getInventory()));
    }
}
