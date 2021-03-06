package com.arematics.minecraft.pvp.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.ItemSword;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
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
                .filter(item -> isArmor(item) || isWeapon(item))
                .forEach(item -> item.setDurability((short) 0));


        sender.info("player_items_repaired").handle();
    }

    private boolean isArmor(ItemStack itemStack){
        return (CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor);
    }

    private boolean isWeapon(ItemStack itemStack) {
        return (CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemSword) ||
                (CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemBow);
    }

}
