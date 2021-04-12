package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.redis.ModeRedisMessagePublisher;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerLeaveInventorySaveListener implements Listener {

    private final ModeRedisMessagePublisher modeRedisMessagePublisher;
    private final ItemCollectionService itemCollectionService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.getInventory().setContents(itemCollectionService.findOrCreate(player.getUniqueId() + ".playerInv")
                .getItems());
        player.getInventory().setArmorContents(itemCollectionService.findOrCreate(player.getUniqueId() + ".armor")
                .getItems());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        ArematicsExecutor.runAsync(() -> savePlayerData(event.getPlayer()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        ArematicsExecutor.runAsync(() -> savePlayerData(event.getPlayer()));
    }

    private void savePlayerData(Player player){
        saveInventory(player, "playerInv", player.getInventory().getContents());
        saveInventory(player, "armor", player.getInventory().getArmorContents());
    }

    private void saveInventory(Player player, String key, ItemStack[] contents){
        ItemCollection collection = itemCollectionService.findOrCreate(player.getUniqueId() + "." + key);
        collection.setItems(CoreItem.create(contents));
        itemCollectionService.save(collection);
        this.modeRedisMessagePublisher.publish(itemCollectionService.messageKey(), player.getUniqueId().toString());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        ArematicsExecutor.runAsync(() -> {
            saveInventory(player, "playerInv", new CoreItem[]{});
            saveInventory(player, "armor", new CoreItem[]{});
        });
        player.getPlayer().getInventory().clear();
    }
}
