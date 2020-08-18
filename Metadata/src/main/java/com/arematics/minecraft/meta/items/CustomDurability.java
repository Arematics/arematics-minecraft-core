package com.arematics.minecraft.meta.items;

import com.arematics.minecraft.meta.events.DurabilityLoseEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CustomDurability implements Listener {

    public static final String DURABILITY_NBT_PREFIX = "C-DURABILITY";

    @EventHandler
    public void onUse(DurabilityLoseEvent event){
        ItemStack itemStack = event.ITEMSTACK;
        NBTItem item = new NBTItem(itemStack);
        int customDurability = item.getInteger(DURABILITY_NBT_PREFIX) - event.LOSE_AMOUNT;
        item.setInteger(DURABILITY_NBT_PREFIX, customDurability);
        item.applyNBT(itemStack);

        NBTItem renewd = new NBTItem(itemStack);
        System.out.println(renewd.getInteger(DURABILITY_NBT_PREFIX));
    }
}
