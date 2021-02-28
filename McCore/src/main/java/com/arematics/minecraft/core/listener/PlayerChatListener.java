package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.AsyncChatMessageEvent;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
        if(!chatEvent.isCancelled()){
            CorePlayer player = CorePlayer.get(chatEvent.getPlayer());
            AsyncChatMessageEvent chatMessageEvent = new AsyncChatMessageEvent(player, chatEvent.getMessage());
            Bukkit.getServer().getPluginManager().callEvent(chatMessageEvent);
            chatEvent.setCancelled(true);
        }
    }

}
