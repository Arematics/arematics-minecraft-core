package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BlockWithoutSpringListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        CoreBoot coreBoot = Boots.getBoot(CoreBoot.class);
        if(!coreBoot.isSpringInitialized())
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    LanguageAPI.prepareRawMessage(event.getPlayer(), "server_starting"));
    }
}
