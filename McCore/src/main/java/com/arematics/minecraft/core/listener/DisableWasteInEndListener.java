package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DisableWasteInEndListener implements Listener{

	public static ArrayList<Material> deneyedInteracts = new ArrayList<Material>(){{
		add(Material.LAVA_BUCKET);
		add(Material.WATER_BUCKET);
		add(Material.BEDROCK);
		add(Material.LAVA);
		add(Material.WATER);
		add(Material.STATIONARY_LAVA);
		add(Material.STATIONARY_WATER);
		add(Material.NETHERRACK);
		add(Material.SPONGE);
	}};

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInteract(PlayerInteractEvent e){
		if(!e.getPlayer().getWorld().getName().contains("the_end")) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		CorePlayer player = CorePlayer.get(e.getPlayer());
		if(player.hasPermission("world.interact.blocks.modifyend")) return;

		if(deneyedInteracts.contains(e.getPlayer().getItemInHand().getType())){
			player.warn("Not allowed to place this").handle();
			e.setCancelled(true);
		}
	}

}
