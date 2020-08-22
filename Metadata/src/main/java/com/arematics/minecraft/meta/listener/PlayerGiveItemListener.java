package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.core.utils.Base64Utils;
import com.arematics.minecraft.meta.NbtProperties;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGiveItemListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        NBTItem item = new NBTItem(itemStack);
        item.setInteger(NbtProperties.DURABILITY_NBT_PREFIX, 10);
        item.setBoolean(NbtProperties.NOT_ENCHTABLE_PREFIX, true);
        String content = Base64Utils.toBase64(new ItemStack[]{new ItemStack(Material.DIAMOND),
                new ItemStack(Material.DIAMOND_BLOCK)});
        item.setString(NbtProperties.ONLY_REPAIR_WITH, content);
        item.applyNBT(itemStack);

        player.getInventory().addItem(itemStack);
    }
}
