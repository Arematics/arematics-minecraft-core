package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.Ammo;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ReloadInterruptOnInventoryOpenListener implements Listener {
    private final Ammo ammo;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOpen(InventoryOpenEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        Map<CorePlayer, BukkitTask> tasks = ammo.getReloading();
        if(tasks.containsKey(player)){
            BukkitTask task = tasks.get(player);
            player.actionBar().sendActionBar("§c§lReload interrupted");
            ammo.getReloading().remove(player);
            task.cancel();
        }
    }
}
