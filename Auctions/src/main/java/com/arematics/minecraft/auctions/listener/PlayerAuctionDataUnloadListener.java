package com.arematics.minecraft.auctions.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.service.PlayerAuctionSettingsService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerAuctionDataUnloadListener implements Listener {

    private final PlayerAuctionSettingsService playerAuctionSettingsService;

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(player.getUser().getConfigurations().containsKey("auction-saving")){
            Configuration value = player.getUser().getConfigurations().get("auction-saving");
            if(!value.getValue().equals("database")) {
                try {
                    playerAuctionSettingsService.delete(playerAuctionSettingsService.findById(player.getUUID()));
                } catch (Exception ignore) {}
            }
        }else{
            try{
                playerAuctionSettingsService.delete(playerAuctionSettingsService.findById(player.getUUID()));
            }catch (Exception ignore){}
        }
    }
}
