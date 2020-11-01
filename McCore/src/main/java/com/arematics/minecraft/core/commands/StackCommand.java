package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.sk89q.worldedit.blocks.ItemType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StackCommand extends CoreCommand {

    public StackCommand(){
        super("stack");
    }

    @Override
    public boolean onDefaultExecute(CommandSender sender){
        List<String> subCommands = super.getSubCommands();
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", subCommands.toArray(new String[]{}))
                .setHover(HoverAction.SHOW_TEXT, "Open to chat")
                .setClick(ClickAction.SUGGEST_COMMAND, "/stack %value%")
                .END()
                .handle();
        return true;
    }

    @SubCommand("items")
    public boolean stackItems(Player player) {
        stackItems(player.getInventory(), true);
        return true;
    }

    public static void stackItems(Inventory inv, boolean force) {
        ItemStack[] items = inv.getContents();

        boolean ignoreMax = /*player.hasPermission("worldguard.stack.illegitimate");*/ true;
        boolean ignoreDamaged = /*player.hasPermission("worldguard.stack.damaged");*/ false;

        int len = items.length;

        int affected = 0;
        int stacked = 0;

        for (int i = 0; i < len; i++) {
            ItemStack item = items[i];

            if ((item != null) && (item.getAmount() > 0)) {
                int max = 64;

                if (item.getAmount() < max) {
                    int needed = max - item.getAmount();

                    for (int j = i + 1; j < len; j++) {
                        ItemStack item2 = items[j];

                        if ((item2 != null) && (item2.getAmount() > 0)) {
                            if ((item2.getTypeId() == item.getTypeId())) {
                                ItemType.usesDamageValue(item.getTypeId());
                                if (item.getDurability() == item2.getDurability() && (
                                        item.getItemMeta() == null && item2.getItemMeta() == null || item.getItemMeta() != null && allowStackTitle(item, item2, force) && allowStackRepairable(item, item2))) {
                                    if (item2.getAmount() > needed) {
                                        item.setAmount(max);
                                        item2.setAmount(item2.getAmount() - needed);
                                        stacked++;
                                        break;
                                    }

                                    items[j] = null;
                                    item.setAmount(item.getAmount() + item2.getAmount());
                                    needed = max - item.getAmount();

                                    affected++;
                                    stacked++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(affected > 0)
            inv.setContents(items);
    }

    public static boolean allowStackTitle(ItemStack item, ItemStack item2, boolean ignoreTitle){
        try{
            if(item.getItemMeta().equals(item2.getItemMeta())) return true;
            if(ignoreTitle){
                if((item.getItemMeta().getDisplayName() == null) != (item2.getItemMeta().getDisplayName() == null) || !item.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())){
                    ItemMeta im1 = item.getItemMeta().clone();
                    ItemMeta im2 = item2.getItemMeta().clone();
                    im1.setDisplayName(null);
                    im2.setDisplayName(null);
                    if(im1.equals(im2)){
                        return true;
                    }
                }
            }
        }catch(Exception ex){}

        return false;
    }

    public static boolean allowStackRepairable(ItemStack item, ItemStack item2){
        try{
            if(item.getItemMeta().equals(item2.getItemMeta())) return true;
            if(item.getItemMeta() instanceof Repairable && item2.getItemMeta() instanceof Repairable){
                Repairable im1 = (Repairable) item.getItemMeta().clone();
                Repairable im2 = (Repairable) item2.getItemMeta().clone();
                if(im1.hasRepairCost() != im2.hasRepairCost() || im1.getRepairCost() != im2.getRepairCost()){
                    int cost = 0;
                    if((im1.hasRepairCost() && im2.hasRepairCost()) || (im1.hasRepairCost() && im1.getRepairCost() > im2.getRepairCost()))
                        cost = im1.getRepairCost();
                    if((im2.hasRepairCost() && im1.hasRepairCost()) || (im2.hasRepairCost() && im1.getRepairCost() > im1.getRepairCost()))
                        cost = im2.getRepairCost();
                    im1.setRepairCost(cost);
                    im2.setRepairCost(cost);
                    if(im1.equals(im2)){
                        {
                            Repairable r = (Repairable) item.getItemMeta();
                            r.setRepairCost(cost);
                            item.setItemMeta((ItemMeta) r);
                        }
                        {
                            Repairable r = (Repairable) item2.getItemMeta();
                            r.setRepairCost(cost);
                            item2.setItemMeta((ItemMeta) r);
                        }
                        return true;
                    }
                }
            }
        }catch(Exception ex){ex.printStackTrace();}
        return false;
    }
}
