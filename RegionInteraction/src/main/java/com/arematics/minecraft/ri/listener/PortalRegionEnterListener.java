package com.arematics.minecraft.ri.listener;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.listener.RelogCooldownListener;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PortalRegionEnterListener implements Listener {

    private final SpawnCommand spawnCommand;
    private final RelogCooldownListener relogCooldownListener;

    @EventHandler
    public void onEnter(RegionEnteredEvent event){
        String id = event.getRegion().getId();
        if(id.startsWith("mode_")) {
            try {
                Warp warp = this.spawnCommand.getWarpService().getWarp(this.spawnCommand.getCurrentTeleport());
                CorePlayer player = event.getPlayer();
                String target = id.replace("mode_" , "");
                player.interact().instantTeleport(warp.getLocation()).schedule();
                if(player.getUser().getCurrentServer().equals("pvp") || player.getUser().getCurrentServer().equals("farmworld")) {
                    if (target.equals("pvp") || target.equals("farmworld"))
                        relogCooldownListener.getJustOnline().remove(event.getPlayer().getUUID());
                }
                MessagingUtils.sendToServer(event.getPlayer(), id.replace("mode_", ""));
            }catch (Exception e){
                event.getPlayer().warn("To your safety mode change has been cancelled").handle();
            }
        }
    }
}
