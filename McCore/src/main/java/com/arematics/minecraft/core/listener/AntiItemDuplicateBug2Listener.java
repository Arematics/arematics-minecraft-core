package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AntiItemDuplicateBug2Listener implements Listener{

	@EventHandler(priority = EventPriority.HIGH)
	public void onDispense(BlockDispenseEvent e){
		boolean bug = false;
		if(e.getBlock().getState() instanceof Dropper){
			Dropper dropper = (Dropper) e.getBlock().getState();
			if(checkInventory(dropper.getInventory())) bug = true;
		}else if(e.getBlock().getState() instanceof Dispenser){
			Dispenser dispenser = (Dispenser) e.getBlock().getState();
			if(checkInventory(dispenser.getInventory())) bug = true;
		}

		if(bug){
			e.setCancelled(true);
			e.getBlock().getWorld().createExplosion(e.getBlock().getLocation().add(0.5, 0.5, 0.5), 10, true);
			e.getBlock().getState().setType(Material.AIR);
			e.getBlock().getState().update(true);
			e.getBlock().getWorld().playEffect(e.getBlock().getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e){
		checkInventory(e.getInventory());
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		if(!p.isOp())
			ArematicsExecutor.syncDelayed(() -> {
				if(!p.isOnline()) return;
				checkInventory(p.getInventory());
				checkInventory(p.getEnderChest());
			}, 500, TimeUnit.MILLISECONDS);
	}

	public boolean checkInventory(Inventory inv){
		boolean bug = false;
		for(int i = 0; i < inv.getContents().length; i++){
			try{
				if(inv.getItem(i).getAmount() < 0){
					bug = true;
					inv.setItem(i, new ItemStack(Material.AIR));
				}
			}catch(Exception e){}
		}
		return bug;
	}

}
