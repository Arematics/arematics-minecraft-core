package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
        Player player = chatEvent.getPlayer();
        ChatAPI.chat(player, chatEvent.getMessage());
        chatEvent.setCancelled(true);
    }

}
