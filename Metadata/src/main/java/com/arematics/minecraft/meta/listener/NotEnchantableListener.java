package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.utils.Base64Utils;
import com.arematics.minecraft.meta.NbtProperties;
import com.arematics.minecraft.meta.events.PrepareAnvilEvent;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@SuppressWarnings("unused")
public class NotEnchantableListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event){
        NBTItem item = new NBTItem(event.getItem());
        if(item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)) event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event){
        NBTItem item = new NBTItem(event.getItem());
        if(item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)) event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event){
        AnvilInventory inventory = event.getAnvil();
        event.setCancelled(!Arrays.stream(inventory.getContents())
                .map(CoreItem::create)
                .allMatch(item -> isMatch(item, event.getItem())));
    }

    private boolean isMatch(CoreItem content, CoreItem input){
        return (content == null) || (hasAllowedContent(content, input) && hasAllowedContent(input, content));
    }

    private boolean hasAllowedContent(CoreItem check, CoreItem content){
        if(check.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)){
            if(check.hasKey(NbtProperties.ONLY_REPAIR_WITH)){
                String base = check.getString(NbtProperties.ONLY_REPAIR_WITH);
                ItemStack[] contents = Base64Utils.fromBase64(base);
                return Arrays.stream(contents).anyMatch(item -> item.isSimilar(content.getItem()));
            }
            return false;
        }
        return true;
    }
}
