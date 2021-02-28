package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.InteractType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomInteractEventListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        CorePlayer player = CorePlayer.get(event.getPlayer());
        boolean sneaking = player.getPlayer().isSneaking();
        InteractType type;
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
            type = sneaking ? InteractType.SHIFT_RIGHT_CLICK : InteractType.RIGHT_CLICK;
        else
            type = sneaking ? InteractType.SHIFT_LEFT_CLICK : InteractType.LEFT_CLICK;
        com.arematics.minecraft.core.events.PlayerInteractEvent clone =
                new com.arematics.minecraft.core.events.PlayerInteractEvent(player,
                        type,
                        action,
                        event.getClickedBlock(),
                        event.getBlockFace(),
                        event.useInteractedBlock(),
                        event.useItemInHand());
        Boots.getBoot(CoreBoot.class).getServer().getPluginManager().callEvent(clone);
        event.setCancelled(clone.isCancelled());
    }
}
