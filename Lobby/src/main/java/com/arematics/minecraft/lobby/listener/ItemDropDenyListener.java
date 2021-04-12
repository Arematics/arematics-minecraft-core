package com.arematics.minecraft.lobby.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.springframework.stereotype.Component;

@Component
public class ItemDropDenyListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        event.setCancelled(!player.isIgnoreMeta());
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        event.setCancelled(true);
    }
}
