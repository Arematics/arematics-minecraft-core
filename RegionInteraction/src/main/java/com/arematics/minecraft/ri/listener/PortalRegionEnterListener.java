package com.arematics.minecraft.ri.listener;

import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class PortalRegionEnterListener implements Listener {

    @EventHandler
    public void onEnter(RegionEnteredEvent event){
        String id = event.getRegion().getId();
        if(id.startsWith("mode_"))
            MessagingUtils.sendToServer(event.getPlayer(), id.replace("mode_", ""));
    }
}
