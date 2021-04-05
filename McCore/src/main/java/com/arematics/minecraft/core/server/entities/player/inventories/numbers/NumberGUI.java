package com.arematics.minecraft.core.server.entities.player.inventories.numbers;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class NumberGUI {

    private final String key;
    private final boolean allowDouble;
    private final CorePlayer player;
    private final List<PositionItem> codex = new ArrayList<>();
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
        CoreItem close = CoreItem.generate(Material.EMERALD_BLOCK)
                .setName("§aSend number")
                .addToLore("§8Value: §e" + value);
        this.builder = InventoryBuilder.create("Select Number", 3)
                .openBlocked(player)
                .fillOuterLine()
                .addItem(close, 3, 5);
        player.inventories().onSlotClick((inv, item) ->
                ArematicsExecutor.asyncDelayed(this::refreshInventory, 100, TimeUnit.MILLISECONDS), Range.allInRow(2));
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
            PositionItem item = new PositionItem((float) val, Items.NUMBERS.get(val));
            this.codex.add(0, item);
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
        CoreItem moveLeft = CoreItem.generate(Material.ARROW)
                .setName("Move Left");
        CoreItem moveRight = CoreItem.generate(Material.ARROW)
                .setName("Move Right");
        if(codex.size() - 1 > end){
            builder.addItem(moveRight, 3, 9);
            player.inventories().registerItemClick(moveRight, () -> {
                this.currentStart += 1;
                this.refreshInventory();
            });
        }else builder.fillWithPlaceholder(Range.custom(9*2 + 8));
        if(currentStart > 0){
            builder.addItem(moveLeft, 3, 1);
            player.inventories().registerItemClick(moveLeft, () -> {
                this.currentStart -= 1;
                this.refreshInventory();
            });
        }else builder.fillWithPlaceholder(Range.custom(9*2));
    }

    private CoreItem prepareItem(int index){
        PositionItem positionItem = codex.get(index);
        double changeVal = Math.pow(10, ((codex.size() - 1) - index));
        CoreItem item = CoreItem.create(positionItem.item).addToLore("Index: " + index);
        player.inventories().registerItemClick(item, ClickType.RIGHT, (i) -> {
            if(value - changeVal < min) return i;
            value -= changeVal;
            return i;
        });
        player.inventories().registerItemClick(item, ClickType.LEFT, (i) -> {
            if(value + changeVal > max) return i;
            value += changeVal;
            return i;
        });
        return item;
    }

    public void closeInventory(boolean fromEvent){
        if(!fromEvent && player.inventories().getView().getTopInventory() != null &&
                player.inventories().getView().getTopInventory().equals(builder.fetchInventory()))
            player.getPlayer().closeInventory();
        this.closeHandler.onClose(player, value);
        HandlerList.unregisterAll(guiListener);
    }

    @RequiredArgsConstructor
    private static class PositionItem{
        private final float number;
        private final CoreItem item;
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
