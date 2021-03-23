package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.ItemUpdateClickListener;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.IntegerBox;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.EnumUtils;
import com.arematics.minecraft.core.utils.Inventories;
import com.arematics.minecraft.data.service.InventoryService;
import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
public class InventoryHandler {

    private static InventoryService inventoryService;
    private static Server server;

    private final CorePlayer player;

    private int page;
    private boolean resetOnClose;

    private final List<ItemUpdateClickListener> itemUpdateClickListeners = new ArrayList<>();
    private Consumer<Inventory> emptySlotClick;
    private Consumer<Inventory> slotClick;
    private Consumer<CoreItem> ownInvClick;
    private IntegerBox slots;

    private BukkitTask updateTask;

    private Runnable refresher;

    private Map<Class<?>, Enum<?>> currentEnums = new HashMap<>();

    InventoryHandler(CorePlayer player){
        this.player = player;
        if(InventoryHandler.inventoryService == null){
            InventoryHandler.inventoryService = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        }
        if(InventoryHandler.server == null){
            InventoryHandler.server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
        }
    }

    public <E extends Enum<E>> void addEnum(E enumValue){
        Class<E> enumClass = enumValue.getDeclaringClass();
        ArematicsExecutor.asyncDelayed(() -> this.currentEnums.put(enumClass, enumValue), 250, TimeUnit.MILLISECONDS);
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
    public void openInventory(Inventory inventory){
        Inventories.openLowerDisabledInventory(inventory, player);
    }

    /**
     * Open inventory for player. Both inventories are blocked
     * @param inventory Inventory to open
     */
    public void openTotalBlockedInventory(Inventory inventory){
        Inventories.openTotalBlockedInventory(inventory, player);
    }
    /**
     * Open inventory for player. Both inventories are enabled
     * @param inventory Inventory to open
     */
    public void openLowerEnabledInventory(Inventory inventory){
        Inventories.openInventory(inventory, player);
    }


    public Inventory getInventory(String key) throws RuntimeException{
        return InventoryHandler.inventoryService.getInventory(player.getUUID() + "." + key);
    }

    public Inventory getOrCreateInventory(String key, String title, byte slots){
        return InventoryHandler.inventoryService.getOrCreate(player.getUUID() + "." + key, title, slots);
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
        ArematicsExecutor.syncDelayed(() -> server.registerItemListener(player, item, action), 250,
                TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler unregisterItemListeners(CoreItem item){
        List<ItemUpdateClickListener> listeners = itemUpdateClickListeners.stream()
                .filter(listener -> item.isSimilar(listener.getItem()))
                .collect(Collectors.toList());
        listeners.forEach(listener -> ArematicsExecutor.syncRun(() -> server.tearDownItemListener(listener)));
        return this;
    }

    public InventoryHandler registerItemClick(CoreItem item, Runnable run){
        return registerItemClick(item, (click) -> {
            run.run();
            return click;
        });
    }

    public InventoryHandler enableRefreshTask(){
        ArematicsExecutor.asyncDelayed(() -> this.updateTask = refresher != null ? ArematicsExecutor.asyncRepeat(this.refresher, 1, 1, TimeUnit.SECONDS) : null,
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
        ArematicsExecutor.asyncDelayed(() -> this.refresher = runnable, 150, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onEmptySlotClick(Consumer<Inventory> click){
        ArematicsExecutor.asyncDelayed(() -> this.emptySlotClick = click, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onItemInOwnInvClick(Consumer<CoreItem> click){
        ArematicsExecutor.asyncDelayed(() -> this.ownInvClick = click, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler onSlotClick(Consumer<Inventory> click, IntegerBox slots){
        ArematicsExecutor.asyncDelayed(() -> {
            this.slotClick = click;
            this.slots = slots;
        }, 200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler addListener(ItemUpdateClickListener listener){
        this.itemUpdateClickListeners.add(listener);
        return this;
    }

    public InventoryHandler tearDownListeners(){
        Server server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
        this.itemUpdateClickListeners.forEach(server::tearDownItemListener);
        this.itemUpdateClickListeners.clear();
        return this;
    }

}
