package com.arematics.minecraft.watcher.listener;

import com.arematics.minecraft.core.CoreEngine;
import com.arematics.minecraft.core.Engine;
import com.arematics.minecraft.watcher.analyses.ChatAnalyses;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatAnalyserListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String message = event.getMessage();
        String[] chatAnalyses = ChatAnalyses.doChatMessageCheck(message);

        if(chatAnalyses.length != 0) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player.isOp()) player.sendMessage(Engine.getEngine(CoreEngine.class).getConfig().getPrefix() +
                        "Found possible chat breaks: " +
                        StringUtils.join(chatAnalyses, ", "));
            });
        }
    }
}
