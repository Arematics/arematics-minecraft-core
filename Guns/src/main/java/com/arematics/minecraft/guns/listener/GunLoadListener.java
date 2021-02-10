package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.Ammo;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GunLoadListener implements Listener {

    private final Ammo ammo;

    @EventHandler
    public void onLoad(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        CoreItem hand = player.getItemInHand();
        if(hand == null || !hand.getMeta().hasKey("weapon")) return;
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            ammo.loadGun(player, ammo.fetchGun(hand));
        }
    }
}
