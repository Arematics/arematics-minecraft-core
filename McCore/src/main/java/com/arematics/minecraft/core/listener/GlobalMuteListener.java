package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.commands.GlobalMuteCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.MuteService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GlobalMuteListener implements Listener {

    private final GlobalMuteCommand globalMuteCommand;
    private final MuteService muteService;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGlobalMute(AsyncPlayerChatEvent e) {
        if (globalMuteCommand.getGlobalMuteStatus() || muteService.isMuted(e.getPlayer().getUniqueId())) {
            if (!CorePlayer.get(e.getPlayer()).getUser().getRank().isInTeam()) e.setCancelled(true);
        }
    }
}
