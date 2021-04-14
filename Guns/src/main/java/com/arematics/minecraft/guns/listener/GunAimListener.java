package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.events.PlayerInteractEvent;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.InteractType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.Ammo;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GunAimListener implements Listener {

    private final Ammo ammo;

    @EventHandler
    public void onLoad(PlayerInteractEvent event){
        CorePlayer player = event.getPlayer();
        CoreItem hand = player.getItemInHand();

        if(hand != null && hand.getMeta().hasKey("weapon") && event.getType() == InteractType.LEFT_CLICK){
            ammo.toggleAim(player);
        }
    }
}
