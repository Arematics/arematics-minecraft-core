package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.meta.NbtProperties;
import com.arematics.minecraft.meta.events.DurabilityLoseEvent;
import com.arematics.minecraft.meta.events.PrepareAnvilEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class CustomDurabilityListener implements Listener {

    @EventHandler
    public void onUse(DurabilityLoseEvent event){
        CoreItem item = event.getItem();
        int customDurability = item.getInteger(NbtProperties.DURABILITY_NBT_PREFIX) - event.getLoseAmount();
        if(customDurability < 0) return;
        item.setInteger(NbtProperties.DURABILITY_NBT_PREFIX, customDurability);
        item.applyNBT(item.getItem());
        event.setCancelled(true);
    }
}
