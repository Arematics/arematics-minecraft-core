package com.arematics.minecraft.core.listener;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.springframework.stereotype.Component;

@Component
public class AntiItemBugListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH)
	public void onDispense(BlockDispenseEvent e){
		if(e.getItem().getType().getId() == 342 || e.getItem().getType().getId() == 343 || e.getItem().getType().getId() == 408){
			e.setCancelled(true);
			e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.FIZZ, 1, 1);
		}
	}

}
