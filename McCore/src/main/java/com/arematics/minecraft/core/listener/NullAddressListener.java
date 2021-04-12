package com.arematics.minecraft.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class NullAddressListener implements Listener {

    @EventHandler
    public void onJoin(final AsyncPlayerPreLoginEvent event){
        final InetAddress address = event.getAddress();

        if (address == null) event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§eNull address not allowed");
    }
}
