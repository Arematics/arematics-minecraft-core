package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.AsyncChatMessageEvent;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AsyncChatListener implements Listener {

    private final Server server;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncChatMessageEvent event){
        List<CorePlayer> players = server.getOnline();
        if(event.isCancelled()) players = server.getOnlineTeam();
        String msg = msg(event.getPlayer(), event.isCancelled(), event.getMessage());
        players.forEach(user -> ArematicsExecutor.runAsync(() -> sendMessage(msg, user)));
    }

    private void sendMessage(String msg, CorePlayer player){
        player.info(msg)
                .DEFAULT()
                .disableServerPrefix()
                .handle();
    }

    private String msg(CorePlayer player, boolean blocked, String message){
        return (blocked ? "§4§l! " : "") + player.getChatMessage().replace("%message%", createChatMessage(player, message));
    }

    private String createChatMessage(CorePlayer player, String message){
        return player.getCachedRank().isInTeam() ? ChatColor.translateAlternateColorCodes('&', message) : message;
    }
}
