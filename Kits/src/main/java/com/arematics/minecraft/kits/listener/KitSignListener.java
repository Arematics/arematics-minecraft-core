package com.arematics.minecraft.kits.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

@Component
public class KitSignListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
                (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN)){
            Sign clicked = (Sign) event.getClickedBlock().getState();
            if(clicked.getLine(0).equals("§8[§6KIT§8]")){
                String rawKit = clicked.getLine(1);
                player.dispatchCommand("/kit " + rawKit);
            }
        }
    }
}
