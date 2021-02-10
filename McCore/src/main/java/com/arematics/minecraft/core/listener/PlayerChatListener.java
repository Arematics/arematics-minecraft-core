package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.controller.ChatController;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerChatListener implements Listener {

    private final ChatController chatController;

    @Autowired
    public PlayerChatListener(ChatController chatController) {
        this.chatController = chatController;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
        if(!chatEvent.isCancelled()){
            CorePlayer player = CorePlayer.get(chatEvent.getPlayer());
            chatController.chat(player, chatEvent.getMessage());
            chatEvent.setCancelled(true);
        }
    }

}
