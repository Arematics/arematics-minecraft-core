package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "invsee", description = "See an other players inventory")
public class InvseeCommand extends CoreCommand {
    public InvseeCommand() {
        super("invsee");
    }

    @SubCommand("{player}")
    public void seeInventory(CorePlayer player, CorePlayer target) {
        Permissions.check(player.getPlayer(),"invsee-change")
                .ifPermitted(sender -> player.openLowerEnabledInventory(target.getPlayer().getInventory()))
                .orElse(sender -> player.openTotalBlockedInventory(target.getPlayer().getInventory()));
    }
}
