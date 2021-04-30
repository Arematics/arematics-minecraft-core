package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class UserQuitListener implements Listener {

    private final Tablist tablist;

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(player.interact().inFight())
            player.getPlayer().setHealth(0.0D);
        this.tablist.remove(player);
    }
}
