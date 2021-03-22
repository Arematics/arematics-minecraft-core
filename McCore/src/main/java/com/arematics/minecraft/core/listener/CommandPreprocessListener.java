package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.CommandRedirect;
import com.arematics.minecraft.data.service.CommandRedirectService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class CommandPreprocessListener implements Listener {

    private final CommandRedirectService commandRedirectService;

    @EventHandler
    public void onPreProcess(PlayerCommandPreprocessEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        String[] split = event.getMessage().split(" ")[0].split(":");
        String cmd = split[0];
        if(split.length > 1) cmd = split[1];
        cmd = cmd.replaceAll("/", "");
        CommandRedirect redirect = commandRedirectService.findRedirect(cmd);
        if(redirect != null){
            if(redirect.getRedirectCmd() != null)
                event.setMessage(redirect.getRedirectCmd());
            else{
                if(redirect.getMessage() != null) player.warn(redirect.getMessage()).handle();
                event.setCancelled(true);
            }
        }
    }
}
