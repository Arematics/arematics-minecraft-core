package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
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
import org.springframework.stereotype.Component;

import java.util.Arrays;

@SuppressWarnings("unused")
@Component
public class NotEnchantableListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event){
        CorePlayer player = CorePlayer.get(event.getEnchanter());
        NBTItem item = new NBTItem(event.getItem());
        if(!player.ignoreMeta() && item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event){
        NBTItem item = new NBTItem(event.getItem());
        CorePlayer player = CorePlayer.get(event.getEnchanter());
        if(!player.ignoreMeta() && item.hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event){
        AnvilInventory inventory = event.getAnvil();
        CorePlayer player = CorePlayer.get(event.getEnchanter());
        if(!player.ignoreMeta())
            event.setCancelled(!Arrays.stream(inventory.getContents())
                    .map(CoreItem::create)
                    .allMatch(item -> isMatch(item, event.getItem())));
    }

    private boolean isMatch(CoreItem content, CoreItem input){
        return (content == null) || (hasAllowedContent(content, input) && hasAllowedContent(input, content));
    }

    private boolean hasAllowedContent(CoreItem check, CoreItem content){
        if(check.getMeta().hasKey(NbtProperties.NOT_ENCHTABLE_PREFIX)){
            if(check.getMeta().hasKey(NbtProperties.ONLY_REPAIR_WITH)){
                String base = check.getMeta().getString(NbtProperties.ONLY_REPAIR_WITH);
                ItemStack[] contents = Base64Utils.fromBase64(base);
                return Arrays.stream(contents).anyMatch(item -> item.isSimilar(content));
            }
            return false;
        }
        return true;
    }
}
