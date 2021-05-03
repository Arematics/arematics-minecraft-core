package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.PlayerData;
import com.arematics.minecraft.data.redis.ModeRedisMessagePublisher;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.service.PlayerDataService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerLeaveSaveListener implements Listener {

    private final Server server;
    private final ModeRedisMessagePublisher modeRedisMessagePublisher;
    private final ItemCollectionService itemCollectionService;
    private final PlayerDataService playerDataService;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.getInventory().setContents(itemCollectionService.findOrCreate(player.getUniqueId() + ".playerInv")
                .getItems());
        player.getInventory().setArmorContents(itemCollectionService.findOrCreate(player.getUniqueId() + ".armor")
                .getItems());
        try{
            PlayerData data = playerDataService.findPlayerData(player.getUniqueId());
            player.setHealth(data.getHealth());
            player.setFoodLevel(data.getHunger());
            player.setLevel(data.getLevel());
            player.setExp(data.getXp());
        }catch (RuntimeException ignore){}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event){
        server.schedule().runAsync(() -> savePlayerData(event.getPlayer(), true));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event){
        server.schedule().runAsync(() -> savePlayerData(event.getPlayer(), true));
    }

    public void savePlayerData(Player player, boolean publish){
        saveBothInventoriesAndThenPublish(player, player.getInventory().getContents(),
                player.getInventory().getArmorContents(), publish);
        savePlayerDataAndThenPublish(player, player.getHealth(), player.getFoodLevel(), player.getLevel(), player.getExp(), publish);
    }

    public void saveBothInventoriesAndThenPublish(Player player, ItemStack[] contents, ItemStack[] armor, boolean publish){
        ItemCollection contentCollection = itemCollectionService.findOrCreate(player.getUniqueId() + "." + "playerInv");
        contentCollection.setItems(CoreItem.create(contents));
        itemCollectionService.save(contentCollection);
        ItemCollection armorCollection = itemCollectionService.findOrCreate(player.getUniqueId() + "." + "armor");
        armorCollection.setItems(CoreItem.create(armor));
        itemCollectionService.save(armorCollection);
        if(publish)
            this.modeRedisMessagePublisher.publish(itemCollectionService.messageKey(), player.getUniqueId().toString());
    }

    public void savePlayerDataAndThenPublish(Player player, double health, int hunger, int level, float exp, boolean publish){
        PlayerData data;
        try{
            data = playerDataService.findPlayerData(player.getUniqueId());
        }catch (RuntimeException re){
            data = new PlayerData(player.getUniqueId(), health, hunger, level, exp);
        }
        data.setHealth(health);
        data.setHunger(hunger);
        data.setLevel(level);
        data.setXp(exp);
        playerDataService.save(data);
        if(publish) this.modeRedisMessagePublisher.publish(playerDataService.messageKey(), player.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        ArematicsExecutor.runAsync(() -> saveBothInventoriesAndThenPublish(player, new CoreItem[]{}, new CoreItem[]{}, true));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        savePlayerDataAndThenPublish(player, 20, 10, 0, 0, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void endDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        player.getInventory().clear();
    }
}
