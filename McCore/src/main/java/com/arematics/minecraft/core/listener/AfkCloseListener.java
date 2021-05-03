package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AfkCloseListener implements Listener {

    private final Server server;

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        server.schedule().runAsync(() -> asyncUpdateMove(event));
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event){
        CorePlayer player = server.fetchPlayer(event.getEnchanter());
        player.onlineTime().callAntiAfk();
    }

    @EventHandler
    public void interactProcessor(PlayerInteractEvent event){
        boolean bookOpen = openBookAntiAfk(event);
    }

    private boolean openBookAntiAfk(PlayerInteractEvent event){
        CorePlayer player = server.fetchPlayer(event.getPlayer());
        CoreItem hand = player.interact().getItemInHand();
        if(isInteract(event) && hand != null && hand.getType() == Material.BOOK_AND_QUILL){
            player.onlineTime().callAntiAfk();
            server.schedule().asyncDelayed(player.onlineTime()::callAntiAfk, 1, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    private boolean isInteract(PlayerInteractEvent event){
        return event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    private void asyncUpdateMove(PlayerMoveEvent event){
        Location f = event.getFrom();
        Location t = event.getTo();
        if(f.getBlockX() != t.getBlockX() || f.getBlockY() != t.getBlockY() || f.getBlockZ() != t.getBlockZ()){
            if(!t.getBlock().isLiquid() || t.clone().add(0, -1, 0).getBlock().isLiquid()){
                CorePlayer player = server.fetchPlayer(event.getPlayer());
                player.onlineTime().callAntiAfk();
            }
        }
    }
}
