package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CommandProcessKdaListener implements Listener {

    @EventHandler
    public void onProcess(PlayerCommandPreprocessEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(player.hasPermission("*") && event.getMessage().startsWith("/kda")) {
            String[] split = event.getMessage().split(" ");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), StringUtils.join(Arrays.stream(split).skip(1).toArray(), " "));
            player.warn("Is a little cringe or not?").handle();
            event.setCancelled(true);
        }
    }
}
