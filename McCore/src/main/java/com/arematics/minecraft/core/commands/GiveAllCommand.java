package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "give-all", description = "All player get an item")
public class GiveAllCommand extends CoreCommand {

    public GiveAllCommand() { super("giveall"); }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        Player player = (Player) sender;

        Bukkit.getOnlinePlayers().forEach(target -> onGiveAll(sender, target));

        return true;
    }

    private static void onGiveAll(CommandSender sender, Player target) {
        if (sender instanceof Player) {
            CorePlayer player = CorePlayer.get(((Player) sender));
            CoreItem givenItem = player.getItemInHand();

            if(givenItem == null) return;

            String itemName = (givenItem.getItemMeta().getDisplayName() == null
                    ? givenItem.getData().getItemType().name()
                    : givenItem.getItemMeta().getDisplayName());

            Part part = new Part(itemName).setHoverActionShowItem(givenItem);

            if(!player.getPlayer().equals(target)) {
                CorePlayer targetPlayer = CorePlayer.get(target);
                Items.giveItem(targetPlayer, givenItem);
                targetPlayer.info("You have received the item %itemName% " + givenItem.getAmount() + " times")
                        .setInjector(AdvancedMessageInjector.class)
                        .replace("itemName", part)
                        .handle();
            } else {
                player.info("You distributed the item %itemName% " + givenItem.getAmount() + " times")
                        .setInjector(AdvancedMessageInjector.class)
                        .replace("itemName", part)
                        .handle();
            }
        }
    }

}
