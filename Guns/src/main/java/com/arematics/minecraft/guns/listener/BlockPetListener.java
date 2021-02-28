package com.arematics.minecraft.guns.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.springframework.stereotype.Component;

@Component
public class BlockPetListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Vector vector = player.getVelocity();
        if(event.getFrom().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE){
            vector.setY(vector.getY() + 0.6D);
            player.setVelocity(vector);
        }

        if(event.getFrom().getBlock().getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK){
            player.setVelocity(player.getLocation().getDirection().multiply(4));
            player.setVelocity(new Vector(player.getVelocity().getX(), 1.0D,
                    player.getVelocity().getZ()));
            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0F,
                    1.0F);
        }
    }
}
