package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.ItemUpdateClickListener;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryController;
import com.arematics.minecraft.core.server.entities.player.inventories.OpenStrategy;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.IntegerBox;
import com.arematics.minecraft.core.utils.EnumUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Setter
@Getter
public class InventoryHandler extends PlayerHandler {

    private final Server server;
    private final InventoryController inventoryController;

    private int page;
    private boolean resetOnClose;

    private final List<ItemUpdateClickListener> itemUpdateClickListeners = new ArrayList<>();
    private Consumer<Inventory> emptySlotClick;
    private BiConsumer<Inventory, CoreItem> slotClick;
    private Function<CoreItem, CoreItem> ownInvClick;
    private IntegerBox slots;

    private BukkitTask updateTask;

    private Runnable refresher;
    private boolean clickBlocked;

    private Map<Class<?>, Enum<?>> currentEnums = new HashMap<>();
    private List<WrappedInventory> currentInventories = new ArrayList<>();

    @Autowired
    InventoryHandler(Server server, InventoryController inventoryController){
        this.server = server;
        this.inventoryController = inventoryController;
    }

    public <E extends Enum<E>> void addEnum(E enumValue){
        Class<E> enumClass = enumValue.getDeclaringClass();
        server.schedule().asyncDelayed(() -> this.currentEnums.put(enumClass, enumValue), 250, TimeUnit.MILLISECONDS);
    }

    public <E extends Enum<E>> void addEnumInstant(E enumValue){
        Class<E> enumClass = enumValue.getDeclaringClass();
        this.currentEnums.put(enumClass, enumValue);
    }

    public <E extends Enum<E>> E getEnum(Class<E> enumClass){
        return enumClass.cast(this.currentEnums.get(enumClass));
    }

    public <E extends Enum<E>> E getEnumOrDefault(E defaultValue){
        Class<E> enumClass = defaultValue.getDeclaringClass();
        return enumClass.cast(this.currentEnums.getOrDefault(enumClass, defaultValue));
    }

    public void nextPage(){
        this.page++;
    }

    public void pageBefore(){
        this.page--;
        if(page <= 0) this.page = 0;
    }

    public InventoryHandler resetPages(){
        if(resetOnClose)
            this.page = 0;
        else
            setResetOnClose(true);
        return this;
    }

    public InventoryView getView(){
        return this.player.getPlayer().getOpenInventory();
    }

    /**
     * Open inventory for player. Own inventory is disabled. Opened inventory is enabled
     * @param inventory Inventory to open
     */
    public void openInventory(WrappedInventory inventory){
        inventoryController.open(inventory, OpenStrategy.DEFAULT, player);
        this.currentInventories.add(inventory);
    }

    /**
     * Open inventory for player. Both inventories are blocked
     * @param inventory Inventory to open
     */
    public void openTotalBlockedInventory(WrappedInventory inventory){
        inventoryController.open(inventory, OpenStrategy.TOTAL_BLOCKED, player);
        this.currentInventories.add(inventory);
    }
    /**
     * Open inventory for player. Both inventories are enabled
     * @param inventory Inventory to open
     */
    public void openLowerEnabledInventory(WrappedInventory inventory){
        inventoryController.open(inventory, OpenStrategy.FULL_EDIT, player);
        this.currentInventories.add(inventory);
    }

    /**
     * Open inventory for player. Own inventory is disabled. Opened inventory is enabled
     * @param inventory Inventory to open
     */
    public void openInventory(Inventory inventory){
        inventoryController.openLowerDisabledInventory(inventory, player);
    }

    /**
     * Open inventory for player. Both inventories are blocked
     * @param inventory Inventory to open
     */
    public void openTotalBlockedInventory(Inventory inventory){
        inventoryController.openTotalBlockedInventory(inventory, player);
    }
    /**
     * Open inventory for player. Both inventories are enabled
     * @param inventory Inventory to open
     */
    public void openLowerEnabledInventory(Inventory inventory){
        inventoryController.openInventory(inventory, player);
    }

    public WrappedInventory getOrCreateInventory(String key, String title, byte slots){
        return inventoryController.findOrCreate(player.getUUID().toString() + "." + key, title, slots);
    }

    public <E extends Enum<E>> InventoryHandler registerEnumItemClickWithRefresh(CoreItem item,
                                                                                  E enumValue){
        Class<E> result = enumValue.getDeclaringClass();
        player.inventories().addEnum(enumValue);
        player.inventories().registerItemClick(item, clicked ->
                onEnumItemRefresh(clicked, result));
        return this;
    }


    private <E extends Enum<E>> CoreItem onEnumItemRefresh(CoreItem item, Class<E> enumClass){
        E current = player.inventories().getEnum(enumClass);
        player.inventories().addEnumInstant(EnumUtils.getNext(current));
        if(this.refresher != null) this.refresher.run();
        return item.bindEnumLore(player.inventories().getEnum(enumClass));
    }

    public InventoryHandler registerItemClick(CoreItem item, Function<CoreItem, CoreItem> action){
        server.schedule().syncDelayed(() -> server.registerItemListener(player, item, action), 250,
                TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler registerItemClick(CoreItem item, ClickType type, Function<CoreItem, CoreItem> action){
        server.schedule().syncDelayed(() -> server.registerItemListener(player, item, type, action), 250,
                TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler registerItemClick(CoreItem item, Runnable run){
        return registerItemClick(item, (click) -> {
            run.run();
            return click;
        });
    }

    public InventoryHandler unregisterItemListeners(CoreItem item){
        List<ItemUpdateClickListener> listeners = itemUpdateClickListeners.stream()
                .filter(listener -> item.isSimilar(listener.getItem()))
                .collect(Collectors.toList());
        tearDownListeners(listeners);
        return this;
    }

    public InventoryHandler unregisterAllItemListeners(){
        tearDownListeners();
        return this;
    }

    public InventoryHandler enableRefreshTask(){
        server.schedule().asyncDelayed(() -> this.updateTask = refresher != null ? server.schedule().asyncRepeat(this.refresher, 1, 1, TimeUnit.SECONDS) : null,
                200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler stopRefreshTask(){
        if(this.updateTask != null){
            this.updateTask.cancel();
            this.updateTask = null;
        }
        return this;
    }

    public <E extends Enum<E>> InventoryHandler registerEnumItemClick(CoreItem item, Supplier<E> action){
        registerItemClick(item, eventItem -> eventItem.bindEnumLore(action.get()));
        return this;
    }

    public InventoryHandler registerAndExecuteItemClick(CoreItem item, Function<CoreItem, CoreItem> action){
        CoreItem clone = action.apply(item);
        return registerItemClick(clone, action);
    }

    public InventoryHandler addRefresher(Runnable runnable){
        server.schedule().asyncDelayed(() -> this.refresher = runnable, 150, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onEmptySlotClick(Consumer<Inventory> click){
        server.schedule().asyncDelayed(() -> this.emptySlotClick = click, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onItemInOwnInvClick(Function<CoreItem, CoreItem> click){
        server.schedule().asyncDelayed(() -> this.ownInvClick = click, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onSlotClick(BiConsumer<Inventory, CoreItem> click, IntegerBox slots){
        server.schedule().asyncDelayed(() -> {
            this.slotClick = click;
            this.slots = slots;
        }, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler addListener(ItemUpdateClickListener listener){
        this.itemUpdateClickListeners.add(listener);
        return this;
    }

    public InventoryHandler tearDownListeners(List<ItemUpdateClickListener> listeners){
        listeners.forEach(server::tearDownItemListener);
        this.itemUpdateClickListeners.removeAll(listeners);
        return this;
    }

    public InventoryHandler tearDownListeners(){
        this.itemUpdateClickListeners.forEach(server::tearDownItemListener);
        this.itemUpdateClickListeners.clear();
        return this;
    }

}
