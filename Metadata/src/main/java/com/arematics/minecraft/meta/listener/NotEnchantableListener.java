package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.meta.NbtProperties;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class NotEnchantableListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event){
        NBTItem item = new NBTItem(event.getItem());
        if(item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)) event.setCancelled(true);
    }

    @EventHandler
    public void onAnvilEnchant(PrepareItemEnchantEvent event){
        NBTItem item = new NBTItem(event.getItem());
        if(item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)) event.setCancelled(true);
    }
}
