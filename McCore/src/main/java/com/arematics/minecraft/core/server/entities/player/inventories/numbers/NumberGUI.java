package com.arematics.minecraft.core.server.entities.player.inventories.numbers;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.items.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class NumberGUI {

    private final String key;
    private final boolean allowDouble;
    private final CorePlayer player;
    private final List<CoreItem> codex = new ArrayList<>();
    private final CloseHandler closeHandler;
    private final double min;
    private final double max;
    private double value;
    private int currentStart = 0;
    private InventoryBuilder builder;

    private GUIListener guiListener;

    public NumberGUI(String key, Number startValue, boolean allowDouble, CorePlayer player, CloseHandler closeHandler){
        this(key, startValue, 999999999, startValue, allowDouble, player, closeHandler);
    }

    public NumberGUI(String key, Number min, Number startValue, CorePlayer player, CloseHandler closeHandler){
        this(key, min, 999999999, startValue, true, player, closeHandler);
    }

    public NumberGUI(String key, Number max, Number startValue, boolean allowDouble, CorePlayer player, CloseHandler closeHandler){
        this(key, startValue, max, startValue, allowDouble, player, closeHandler);
    }

    public NumberGUI(String key, Number min, Number max, Number startValue, boolean allowDouble, CorePlayer player, CloseHandler closeHandler){
        this.key = key;
        this.allowDouble = allowDouble;
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.player = player;
        this.value = startValue.doubleValue();
        if(value == 0) value = 1;
        CoreItem info = CoreItem.generate(Material.BOOK)
                .setName("§a" + key)
                .addToLore("§8Select a number value")
                .addToLore("§8Use §eLEFT Click §8to count up")
                .addToLore("§8Use §eRIGHT Click §8to count down")
                .addToLore("§cClose inventory or click on 'send' to end number input");
        CoreItem close = CoreItem.generate(Material.EMERALD_BLOCK)
                .setName("§aSend number")
                .addToLore("§8Value: §e" + value);
        this.builder = InventoryBuilder.create("Select Number", 3, player)
                .openBlocked(player)
                .fillOuterLine()
                .addItem(info, 1, 5)
                .addItem(close, 3, 5);
        player.inventories().onSlotClick((inv, item) -> this.refreshInventory(), Range.allInRow(2));
        this.guiListener = new GUIListener();

        Bukkit.getPluginManager().registerEvents(guiListener, Boots.getBoot(CoreBoot.class));
        this.closeHandler = closeHandler;
        refreshInventory();
    }

    public void refreshInventory(){
        CoreItem close = CoreItem.generate(Material.EMERALD_BLOCK)
                .setName("§aSend number")
                .addToLore("§8Value: §e" + value);
        builder.addItem(close, 3, 5);
        player.inventories().unregisterAllItemListeners();
        player.inventories().registerItemClick(close, () -> this.closeInventory(false));
        calculateCodex();
        setItems();
    }

    public void calculateCodex(){
        this.codex.clear();
        int clone = (int) value;
        while(clone > 0){
            int val = clone % 10;
            this.codex.add(0, Items.NUMBERS.get(val));
            clone = clone / 10;
        }
    }

    public void setItems(){
        int end = Math.min(codex.size(), (currentStart + 9));
        AtomicInteger pos = new AtomicInteger(1);
        IntStream.rangeClosed(1, 9)
                .forEach(index -> builder.addItem(null, 2, index));
        IntStream.range(currentStart, end)
                .forEach(index -> builder.addItem(prepareItem(index), 2, pos.getAndIncrement()));
    }

    private CoreItem prepareItem(int index){
        CoreItem item = CoreItem.create(codex.get(index)).setString("index", "a" + index);
        double changeVal = Math.pow(10, ((codex.size() - 1) - index));
        player.inventories().registerItemClick(item, ClickType.RIGHT, (i) -> upOrDown(i, true, changeVal));
        player.inventories().registerItemClick(item, ClickType.LEFT, (i) -> upOrDown(i, false, changeVal));
        return item;
    }

    private CoreItem upOrDown(CoreItem item, boolean down, double changeVal){
        if(value + changeVal > max || value - changeVal < min) return item;
        if(down) value -= changeVal;
        else value += changeVal;
        return item;
    }

    public void closeInventory(boolean fromEvent){
        if(!fromEvent && player.inventories().getView().getTopInventory() != null &&
                player.inventories().getView().getTopInventory().equals(builder.fetchInventory()))
            player.getPlayer().closeInventory();
        this.closeHandler.onClose(player, value);
        HandlerList.unregisterAll(guiListener);
    }

    private class GUIListener implements Listener {

        @EventHandler
        public void onClose(InventoryCloseEvent event){
            if(event.getPlayer().equals(player.getPlayer()) &&
                    event.getView().getTopInventory().equals(builder.fetchInventory())){
                closeInventory(true);
            }
        }
    }

    public interface CloseHandler{
        void onClose(CorePlayer player, Number result);
    }
}
