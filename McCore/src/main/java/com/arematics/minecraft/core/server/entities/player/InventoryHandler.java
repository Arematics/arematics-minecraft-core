package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.ItemUpdateClickListener;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
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

    public void resetPages(){
        if(resetOnClose)
            this.page = 0;
        else
            setResetOnClose(true);
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
                                                                                  E enumValue,
                                                                                  InventoryBuilder builder,
                                                                                  PageBinder<?> binder){
        Class<E> result = enumValue.getDeclaringClass();
        player.inventories().addEnum(enumValue);
        player.inventories().registerItemClick(item, clicked ->
                onEnumItemRefresh(clicked, result, () -> builder.bindPaging(player, binder, true)));
        return this;
    }


    private <E extends Enum<E>> CoreItem onEnumItemRefresh(CoreItem item, Class<E> enumClass, Runnable run){
        E current = player.inventories().getEnum(enumClass);
        player.inventories().addEnumInstant(EnumUtils.getNext(current));
        run.run();
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

    public InventoryHandler registerRefreshTask(Runnable runnable){
        ArematicsExecutor.asyncDelayed(() -> this.updateTask = ArematicsExecutor.asyncRepeat(runnable,
                1, 1, TimeUnit.SECONDS),
                200, TimeUnit.MILLISECONDS);
        return this;
    }

    public InventoryHandler stopRefreshTask(){
        System.out.println("CLOSE TASK");
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

    public void onEmptySlotClick(Consumer<Inventory> click){
        ArematicsExecutor.asyncDelayed(() -> this.emptySlotClick = click, 200, TimeUnit.MILLISECONDS);
    }

    public void onItemInOwnInvClick(Consumer<CoreItem> click){
        ArematicsExecutor.asyncDelayed(() -> this.ownInvClick = click, 200, TimeUnit.MILLISECONDS);
    }

    public void onSlotClick(Consumer<Inventory> click, IntegerBox slots){
        ArematicsExecutor.asyncDelayed(() -> {
            this.slotClick = click;
            this.slots = slots;
        }, 200, TimeUnit.MILLISECONDS);
    }

    public void addListener(ItemUpdateClickListener listener){
        this.itemUpdateClickListeners.add(listener);
    }

    public void tearDownListeners(){
        Server server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
        this.itemUpdateClickListeners.forEach(server::tearDownItemListener);
        this.itemUpdateClickListeners.clear();
    }

}
