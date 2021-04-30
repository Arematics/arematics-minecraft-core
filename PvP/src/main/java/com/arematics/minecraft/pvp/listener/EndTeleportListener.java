package com.arematics.minecraft.pvp.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class EndTeleportListener implements Listener {

    private final WarpService warpService;

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getPlayer().getWorld().getName().endsWith("_the_end")){
            try{
                ArematicsExecutor.runAsync(() -> {
                    Warp warp = warpService.getWarp("end");
                    player.interact().teleport(warp.getLocation(), true).schedule();
                });
            }catch (Exception ignore){}
        }
    }
}
