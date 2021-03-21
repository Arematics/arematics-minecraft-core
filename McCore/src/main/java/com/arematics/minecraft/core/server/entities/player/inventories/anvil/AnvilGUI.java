package com.arematics.minecraft.core.server.entities.player.inventories.anvil;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilGUI {

    private final Bootstrap bootstrap;
    private final Player holder;
    private final CoreItem insert;
    private final String insertString;
    private final ClickHandler clickHandler;
    private final NmsHelper nms;
    private final int containerId;
    private final Inventory inventory;

    private final ListenUp listener = new ListenUp();

    private boolean open;

    public AnvilGUI(Bootstrap bootstrap, Player holder, String insert, ClickHandler clickHandler) {
        this(bootstrap, holder, insert, "", clickHandler);
    }

    public AnvilGUI(Bootstrap bootstrap, Player holder, String insert, String startValue, ClickHandler clickHandler) {
        this.bootstrap = bootstrap;
        this.holder = holder;
        this.clickHandler = clickHandler;

        final CoreItem paper = CoreItem.create(new ItemStack(Material.PAPER));
        final ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(insert + startValue);
        paper.setItemMeta(paperMeta);
        this.insert = paper;
        this.insertString = insert;

        this.nms = NmsHelper.INSTANCE;

        Bukkit.getPluginManager().registerEvents(listener, bootstrap);

        nms.handleInventoryCloseEvent(holder);
        nms.setActiveContainerDefault(holder);

        final Object container = nms.newContainerAnvil(holder);

        inventory = nms.toBukkitInventory(container);
        inventory.setItem(AnvilSlot.LEFT.getSlot(), this.insert);

        containerId = nms.getNextContainerId(holder);
        nms.sendPacketOpenWindow(holder, containerId);
        nms.setActiveContainer(holder, container);
        nms.setActiveContainerId(container, containerId);
        nms.addActiveContainerSlotListener(container, holder);

        open = true;
    }

    /**
     * Closes the inventory if it's open.
     * @throws IllegalArgumentException If the inventory isn't open
     */
    public void closeInventory(boolean fromEvent) {
        if(!open)
            throw new IllegalArgumentException("You can't close an inventory that isn't open!");
        open = false;

        if(!fromEvent)
            nms.handleInventoryCloseEvent(holder);
        nms.setActiveContainerDefault(holder);
        nms.sendPacketCloseWindow(holder, containerId);

        HandlerList.unregisterAll(listener);
    }

    /**
     * Simply holds the listeners for the GUI
     */
    private class ListenUp implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if(e.getInventory().equals(inventory)) {
                e.setCancelled(true);
                final Player clicker = (Player) e.getWhoClicked();
                if(e.getRawSlot() == AnvilSlot.RESULT.getSlot()) {
                    final CoreItem clicked = CoreItem.create(inventory.getItem(e.getRawSlot()));
                    if(clicked == null || clicked.getType() == Material.AIR) return;
                    final String ret = clickHandler.onClick(clicker, clicked.hasItemMeta() ?
                            clicked.getItemMeta().getDisplayName().replace(insertString, "") :
                            clicked.getType().toString());
                    if(ret != null) {
                        final ItemMeta meta = clicked.getItemMeta();
                        meta.setDisplayName(ret);
                        clicked.setItemMeta(meta);
                        inventory.setItem(AnvilSlot.RESULT.getSlot(), clicked);
                        clicker.updateInventory();
                    } else closeInventory(false);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if(e.getInventory().equals(inventory)) {
                if(e.getInventory().getType() == InventoryType.ANVIL){
                    AnvilInventory inventory = (AnvilInventory) e.getInventory();
                    CoreItem item = CoreItem.create(inventory.getItem(AnvilSlot.RESULT.getSlot()));
                    if(item == null || ( !item.hasItemMeta() || item.getItemMeta().getDisplayName() == null))
                        clickHandler.onClick((Player) e.getPlayer(), null);
                }
                if(open)
                    closeInventory(true);
                e.getInventory().clear();
            }
        }
    }

    public interface ClickHandler{
        String onClick(Player clicker, String input);
    }
}
