package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.redis.ModeRedisMessagePublisher;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerLeaveInventorySaveListener implements Listener {

    private final ModeRedisMessagePublisher modeRedisMessagePublisher;
    private final ItemCollectionService itemCollectionService;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.getPlayer().getInventory().setContents(itemCollectionService.findOrCreate(player.getUUID() + ".playerInv")
                .getItems());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        ItemCollection collection = itemCollectionService.findOrCreate(player.getUUID() + ".playerInv");
        collection.setItems(CoreItem.create(player.getPlayer().getInventory().getContents()));
        itemCollectionService.save(collection);
        this.modeRedisMessagePublisher.publish(itemCollectionService.messageKey(), player.getUUID().toString());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        ItemCollection collection = itemCollectionService.findOrCreate(player.getUUID() + ".playerInv");
        collection.setItems(CoreItem.create(player.getPlayer().getInventory().getContents()));
        itemCollectionService.save(collection);
        this.modeRedisMessagePublisher.publish(itemCollectionService.messageKey(), player.getUUID().toString());
    }
}
