package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
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

            Part part = new Part(itemName).setHoverAction(HoverAction.SHOW_ITEM, givenItem.getMeta().toString());

            if(!player.getPlayer().equals(target)) {
                target.getInventory().addItem(givenItem);
                ((CorePlayer) target).info("You have received the item %itemName% " + givenItem.getAmount() + " times")
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
