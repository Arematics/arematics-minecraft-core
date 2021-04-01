package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.commands.NoEditCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class NoEditListener implements Listener {

    private final NoEditCommand noEditCommand;

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(noEditCommand.getNoEdit().containsKey(player.getUUID())){
            CoreItem item = noEditCommand.getNoEdit().get(player.getUUID());
            if(item != null && player.getItemInHand().isSimilar(item)) event.setCancelled(true);
            else if(item == null) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(noEditCommand.getNoEdit().containsKey(player.getUUID())){
            CoreItem item = noEditCommand.getNoEdit().get(player.getUUID());
            if(item != null && player.getItemInHand().isSimilar(item)) event.setCancelled(true);
            else if(item == null) event.setCancelled(true);
        }
    }
}
