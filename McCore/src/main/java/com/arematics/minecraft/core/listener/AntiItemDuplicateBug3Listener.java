package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AntiItemDuplicateBug3Listener implements Listener {

	private final Server server;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemClick(InventoryClickEvent e){
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(e.getView().getTopInventory() == null || e.getView().getTopInventory().getType() != InventoryType.ENCHANTING)
			return;

		CorePlayer player = server.fetchPlayer((Player) e.getWhoClicked());
		checkEnchanter(player);

		if(e.getClick() == ClickType.NUMBER_KEY){
			e.setCancelled(true);
			player.warn("No number keys could be used in the Enchanter").handle();
			return;
		}

		boolean islapis = e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK &&
				e.getCurrentItem().getDurability() == (short) 4;
		if(e.getCurrentItem() != null && e.getCurrentItem().getAmount() > 1 && !islapis){
			player.warn("Only one item at a time").handle();
			e.setCancelled(true);
		}

	}

	public void checkEnchanter(CorePlayer p) {
		server.schedule().syncDelayed(() -> {
			if(p.getPlayer().getOpenInventory() == null || !(p.getPlayer().getOpenInventory().getTopInventory() instanceof EnchantingInventory))
				return;
			EnchantingInventory einv = (EnchantingInventory) p.getPlayer().getOpenInventory().getTopInventory();
			if(einv.getItem() != null && einv.getItem().getAmount() > 1) {
				p.getPlayer().getInventory().addItem(einv.getItem());
				einv.setItem(null);
				p.getPlayer().updateInventory();
			}
		}, 250, TimeUnit.MILLISECONDS);
	}

}
