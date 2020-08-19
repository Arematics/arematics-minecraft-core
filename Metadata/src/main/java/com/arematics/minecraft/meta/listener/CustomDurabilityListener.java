package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.meta.NbtProperties;
import com.arematics.minecraft.meta.events.DurabilityLoseEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CustomDurabilityListener implements Listener {

    @EventHandler
    public void onUse(DurabilityLoseEvent event){
        ItemStack itemStack = event.ITEMSTACK;
        NBTItem item = new NBTItem(itemStack);
        int customDurability = item.getInteger(NbtProperties.DURABILITY_NBT_PREFIX) - event.LOSE_AMOUNT;
        if(customDurability < 0) return;
        item.setInteger(NbtProperties.DURABILITY_NBT_PREFIX, customDurability);
        item.applyNBT(itemStack);
        event.setCancelled(true);
    }
}
