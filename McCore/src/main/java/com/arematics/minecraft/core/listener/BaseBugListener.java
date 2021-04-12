package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseBugListener implements Listener {

    private static final List<Material> NOT_ALLOWED = new ArrayList<Material>(){{
        add(Material.RAILS);
        add(Material.ACTIVATOR_RAIL);
        add(Material.DETECTOR_RAIL);
        add(Material.POWERED_RAIL);
        add(Material.BED);
        add(Material.BED_BLOCK);
    }};

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        Block u1 = event.getBlock().getRelative(BlockFace.UP);
        Block u2 = u1.getRelative(BlockFace.UP);

        if((u1.getType() == Material.BEDROCK || u2.getType() == Material.BEDROCK) &&
                NOT_ALLOWED.contains(event.getBlockPlaced().getType())){
            event.setCancelled(true);
            Messages.create("not_placeable_bug").WARNING().to(event.getPlayer()).handle();
        }
    }
}
