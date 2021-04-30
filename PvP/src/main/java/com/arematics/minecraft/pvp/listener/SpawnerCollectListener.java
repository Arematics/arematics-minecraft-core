package com.arematics.minecraft.pvp.listener;

import com.arematics.minecraft.core.events.PlayerInteractEvent;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.pvp.commands.SpawnerWrenchCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class SpawnerCollectListener implements Listener {

    private final Server server;

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(event.getBlock().getType() == Material.MOB_SPAWNER){
                CoreItem hand = event.getPlayer().interact().getItemInHand();
                if(hand != null && hand.getMeta().hasKey("wrench") && hand.getMeta().getString("wrench").equals(SpawnerWrenchCommand.WRENCH_META_KEY)){
                    event.getPlayer().interact().setItemInHand(null);
                    event.getBlock().setType(Material.AIR);
                    CoreItem spawner = server.items().generate(Material.MOB_SPAWNER);
                    server.items().giveItemTo(event.getPlayer(), spawner);
                }
            }
        }
    }
}
