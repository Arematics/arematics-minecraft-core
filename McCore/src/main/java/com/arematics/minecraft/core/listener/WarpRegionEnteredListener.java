package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarpRegionEnteredListener implements Listener {

    private final WarpService warpService;

    @Autowired
    public WarpRegionEnteredListener(WarpService warpService){
        this.warpService = warpService;
    }

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent enteredEvent){
        CorePlayer player = CorePlayer.get(enteredEvent.getPlayer());
        ProtectedRegion region = enteredEvent.getRegion();
        String id = region.getId();
        if(id.startsWith("warpto_")){
            String value = id.replace("warpto_", "");
            if(!StringUtils.isBlank(value)){
                try{
                    Warp warp = warpService.getWarp(value);
                    ArematicsExecutor.syncRun(() -> player.getPlayer().teleport(warp.getLocation()));
                }catch (RuntimeException ignored){}
            }
        }
    }
}
