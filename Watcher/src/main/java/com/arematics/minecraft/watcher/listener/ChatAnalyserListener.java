package com.arematics.minecraft.watcher.listener;

import com.arematics.minecraft.core.events.AsyncChatMessageEvent;
import com.arematics.minecraft.watcher.analyses.ChatAnalyses;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ChatAnalyserListener implements Listener {

    private final ChatAnalyses chatAnalyses;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatMessageEvent event){
        String message = event.getMessage();
        event.setCancelled(chatAnalyses.isBlocking(message));
    }
}
