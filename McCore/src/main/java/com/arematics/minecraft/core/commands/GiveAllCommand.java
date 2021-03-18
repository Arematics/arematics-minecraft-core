package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "give-all", description = "All player get an item")
public class GiveAllCommand extends CoreCommand {

    public GiveAllCommand() { super("giveall"); }

    @Override
    protected void onDefaultCLI(CorePlayer sender) {
        Bukkit.getOnlinePlayers().forEach(target -> onGiveAll(sender, target));
    }

    private static void onGiveAll(CorePlayer sender, Player target) {
        if (sender instanceof Player) {
            CoreItem givenItem = sender.getItemInHand();

            if(givenItem == null) return;

            String itemName = (givenItem.getItemMeta().getDisplayName() == null
                    ? givenItem.getData().getItemType().name()
                    : givenItem.getItemMeta().getDisplayName());

            Part part = new Part(itemName).setHoverActionShowItem(givenItem);

            if(!sender.getPlayer().equals(target)) {
                CorePlayer targetPlayer = CorePlayer.get(target);
                Items.giveItem(targetPlayer, givenItem);
                targetPlayer.info("player_item_give_all_received")
                        .setInjector(AdvancedMessageInjector.class)
                        .replace("itemName", part)
                        .replace("amount", new Part(String.valueOf(givenItem.getAmount())))
                        .handle();
            } else {
                sender.info("player_item_give_all_send")
                        .setInjector(AdvancedMessageInjector.class)
                        .replace("itemName", part)
                        .replace("amount", new Part(String.valueOf(givenItem.getAmount())))
                        .handle();
            }
        }
    }

}
