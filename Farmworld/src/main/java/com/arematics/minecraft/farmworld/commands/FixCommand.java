package com.arematics.minecraft.farmworld.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Perm(permission = "world.interact.player.fix", description = "fix your items")
@Component
public class FixCommand extends CoreCommand {

    public FixCommand() {
        super("fix", "fixall", "repair");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Arrays.stream(sender.getPlayer().getInventory().getArmorContents())
                .filter(Objects::nonNull)
                .forEach(item -> item.setDurability((short) 0));

        Arrays.stream(sender.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .map(server.items()::createNoModifier)
                .filter(item -> item.isArmor() || item.isWeapon())
                .forEach(item -> {
                    System.out.println(item);
                    item.setDurability((short) 0);
                });


        sender.info("player_items_repaired").handle();
    }

}
