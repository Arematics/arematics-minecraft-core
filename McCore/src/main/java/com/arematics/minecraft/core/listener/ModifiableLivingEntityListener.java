package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.entities.ModifiedLivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ModifiableLivingEntityListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        Player interacter = event.getPlayer();
        ModifiedLivingEntity entity = new ModifiedLivingEntity((LivingEntity) event.getRightClicked());
        if(entity.hasBindedCommand()) Bukkit.dispatchCommand(interacter, entity.getBindedCommand());
    }
}
