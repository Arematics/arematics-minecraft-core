package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

@Component
public class JoinAndQuitMessageListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        Config config = Boots.getBoot(CoreBoot.class).getPluginConfig();
        if(!event.getPlayer().hasPlayedBefore()){
            event.setJoinMessage("");
            String value = config.findByKey("first_join_message");
            if(Boolean.parseBoolean(value))
                player.info("first_join_message").handle();
        }
        event.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        event.setQuitMessage("");
    }
}
