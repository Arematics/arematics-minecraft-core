package com.arematics.minecraft.core.events;

import com.arematics.minecraft.core.commands.GlobalMuteCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalMuteEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onGlobalMute(AsyncPlayerChatEvent e) {
        if (GlobalMuteCommand.isGlobalMuteActive) {
            if (!CorePlayer.get(e.getPlayer()).getUser().getRank().isInTeam())
                e.setCancelled(true);
        }
    }
}
