package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.Ammo;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GunLoadListener implements Listener {

    private final Ammo ammo;
    private final Server server;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoad(PlayerDropItemEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        CoreItem hand = server.items().createNoModifier(event.getItemDrop().getItemStack());
        if(hand != null && hand.getMeta().hasKey("weapon")){
            ammo.loadGun(player, ammo.fetchGun(hand));
            event.setCancelled(true);
        }
    }
}
