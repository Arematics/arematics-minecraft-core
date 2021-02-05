package com.arematics.minecraft.buffs.listener;

import com.arematics.minecraft.buffs.server.PlayerBuffHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BuffsRegisterListener implements Listener {
    private final PlayerBuffHandler playerBuffHandler;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        playerBuffHandler.enableBuffs(CorePlayer.get(event.getPlayer()));
    }
}
