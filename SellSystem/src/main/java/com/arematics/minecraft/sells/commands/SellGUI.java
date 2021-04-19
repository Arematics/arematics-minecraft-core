package com.arematics.minecraft.sells.commands;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.ItemPrice;
import com.arematics.minecraft.data.service.ItemPriceService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SellGUI {

    private final Server server;
    private final CorePlayer sender;
    private final ItemPriceService itemPriceService;
    private final Map<CoreItem, Double> items = new HashMap<>();

    private CoreItem sellItems;

    private final InventoryBuilder builder;

    private final CloseListener closeListener;
    private final Random random = new Random();

    private boolean blockRemove = false;

    public SellGUI(CorePlayer sender, Server server, ItemPriceService itemPriceService){
        this.sender = sender;
        this.server = server;
        this.itemPriceService = itemPriceService;
        CoreItem showPrices = server.items().generateNoModifier(Material.BOOK)
                .bindCommand("sell list")
                .setName("§aShow item prices");
        builder = InventoryBuilder.create("Sell Items", 6, sender)
                .openBlocked(sender)
                .fillWithPlaceholder(Range.allInRow(1))
                .fillWithPlaceholder(Range.allInRow(6))
                .addItem(showPrices, 6, 3);
        setSellItem();
        sender.inventories().registerItemClick(sellItems, this::sellItems);
        sender.inventories().onItemInOwnInvClick(this::addItem);
        this.closeListener = new CloseListener();
        Bukkit.getPluginManager().registerEvents(closeListener, Boots.getBoot(CoreBoot.class));
    }

    public void refreshInventory(){
        setSellItem();
        InventoryPlaceholder.clear(builder.fetchInventory(), Range.allHardInRows(0, 8, 1, 2, 3, 4));
        sender.inventories().unregisterAllItemListeners();
        sender.inventories().registerItemClick(sellItems, this::sellItems);
        AtomicInteger pos = new AtomicInteger(1);
        items.keySet().forEach(item -> {
            builder.addItem(item, 2, pos.getAndIncrement());
            sender.inventories().registerItemClick(item, this::removeItem);
        });
        sender.inventories().setClickBlocked(false);
    }

    private void setSellItem(){
        sellItems = server.items().generateNoModifier(Material.EMERALD_BLOCK)
                .setName("§aSell items in inventory").addToLore(" ", "§8Sell Price: §e" + CommandUtils.prettyDecimal(sellPrice()) + " Coins");
        builder.addItem(sellItems, 6, 5);
    }

    public CoreItem addItem(CoreItem item){
        if(item.getItemMeta() != null && item.getItemMeta().getLore() != null){
            sender.warn("Item couldn't be sold").handle();
            return item;
        }
        if(items.size() >= 36)
            return item;
        int id = item.getData().getItemTypeId();
        byte data = item.getData().getData();
        String key = id + ":" + data;
        CoreItem result = server.items().create(item).setString("randomKey", "a" + random.nextInt(50000));
        double finalPrice;
        try{
            ItemPrice price = itemPriceService.findItemPrice(key);
            finalPrice = price.getPrice() * item.getAmount();
            result = result.addToLore(" ", "§8Sell Price: §e" + CommandUtils.prettyDecimal(finalPrice) + " Coins");
            items.put(result, finalPrice);
        }catch (Exception e){
            sender.warn("Item has no sell price and couldn't be sold").handle();
            return item;
        }
        ArematicsExecutor.asyncDelayed(this::refreshInventory, 100, TimeUnit.MILLISECONDS);
        return null;
    }

    public CoreItem removeItem(CoreItem item){
        if(sender.inventories().isClickBlocked()) return item;
        if(item == null) return null;
        items.remove(item);
        if(item.getMeta().hasKey("randomKey")) item = item.removeMeta("randomKey");
        if(item.hasItemMeta()) item = item.clearLore();
        server.items().giveItemTo(sender, item);
        sender.inventories().setClickBlocked(true);
        ArematicsExecutor.asyncDelayed(this::refreshInventory, 500, TimeUnit.MILLISECONDS);
        return null;
    }

    private double sellPrice(){
        return items.values().stream().mapToDouble(val -> val).sum();
    }

    public void sellItems(){
        ArematicsExecutor.runAsync(() -> {
            if(sender.inventories().isClickBlocked()) return;
            double price = sellPrice();
            boolean success = server.currencyController()
                    .createEvent(sender)
                    .setAmount(price)
                    .setEventType(CurrencyEventType.GENERATE)
                    .setTarget("sell-items")
                    .addMoney();
            if(success){
                sender.info("Items sold successfully").handle();
                items.clear();
                sender.getPlayer().closeInventory();
                HandlerList.unregisterAll(closeListener);
                sender.inventories().setClickBlocked(false);
            }else{
                sender.warn("Could not sell items").handle();
                closeInventory(false);
            }
        });
    }

    public void closeInventory(boolean fromEvent){
        server.items().giveItemsTo(sender, items.keySet()
                .stream()
                .map(item -> item.removeMeta("randomKey").clearLore())
                .toArray(CoreItem[]::new));
        items.clear();
        if(!fromEvent) sender.getPlayer().closeInventory();
        HandlerList.unregisterAll(closeListener);
        sender.inventories().setClickBlocked(false);
    }

    private class CloseListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onCloseBlock(InventoryCloseEvent event){
            if(event.getPlayer().equals(sender.getPlayer()) &&
                    event.getView().getTopInventory().equals(builder.fetchInventory())){
                sender.inventories().setClickBlocked(true);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event){
            if(event.getPlayer().equals(sender.getPlayer()) &&
                    event.getView().getTopInventory().equals(builder.fetchInventory())){
                closeInventory(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onQuit(PlayerQuitEvent event){
            if(event.getPlayer().equals(sender.getPlayer())
                    && event.getPlayer().getOpenInventory() != null
                    && event.getPlayer().getOpenInventory().getTopInventory().equals(builder.fetchInventory())){
                closeInventory(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onKick(PlayerKickEvent event){
            if(event.getPlayer().equals(sender.getPlayer())
                    && event.getPlayer().getOpenInventory() != null
                    && event.getPlayer().getOpenInventory().getTopInventory().equals(builder.fetchInventory())){
                closeInventory(true);
            }
        }
    }
}
