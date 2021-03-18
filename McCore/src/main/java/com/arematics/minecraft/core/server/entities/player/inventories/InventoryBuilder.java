package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Box;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.IntegerBox;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class InventoryBuilder {

    public static InventoryBuilder create(String title, int rows){
        return new InventoryBuilder(title, rows);
    }

    private final Inventory inv;
    private final String title;
    private final int size;
    private DyeColor dyeColor = DyeColor.BLACK;

    private InventoryBuilder(String title, int rows){
        this.title = "§8" + title;
        this.size = rows * 9;
        this.inv = Bukkit.createInventory(null, size, title);
    }

    public InventoryBuilder setPreferredDyeColor(DyeColor dyeColor){
        this.dyeColor = dyeColor;
        return this;
    }

    public InventoryBuilder fillWithPlaceholder(IntegerBox box){
        CoreItem glassPane = CoreItem.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, dyeColor.getData()));
        glassPane.disableClick().setName("§8 ");
        box.toList().forEach(index -> inv.setItem(index, glassPane));
        return this;
    }

    public InventoryBuilder open(CorePlayer... players){
        Arrays.stream(players).forEach(player -> player.inventories().openInventory(inv));
        return this;
    }

    public InventoryBuilder openLowerEnabled(CorePlayer... players){
        Arrays.stream(players).forEach(player -> player.inventories().openLowerEnabledInventory(inv));
        return this;
    }

    public InventoryBuilder openBlocked(CorePlayer... players){
        Arrays.stream(players).forEach(player -> player.inventories().openTotalBlockedInventory(inv));
        return this;
    }

    public InventoryBuilder open(String name, CorePlayer... players){
        Arrays.stream(players).forEach(player -> {
            if(player.inventories().getView().getTopInventory() != null &&
                    player.inventories().getView().getTopInventory().getName().equals(name))
                player.inventories().setResetOnClose(false);
            player.inventories().openInventory(inv);
        });
        return this;
    }

    public InventoryBuilder openBlocked(String name, CorePlayer... players){
        Arrays.stream(players).forEach(player -> {
            if(player.inventories().getView().getTopInventory() != null &&
                    player.inventories().getView().getTopInventory().getName().equals(name))
                player.inventories().setResetOnClose(false);
            player.inventories().openTotalBlockedInventory(inv);
        });
        return this;
    }

    public InventoryBuilder fillAll(){
        CoreItem glassPane = CoreItem.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, dyeColor.getData()));
        glassPane.disableClick().setName("§8 ");
        IntStream.range(0, inv.getSize()).forEach(index -> inv.setItem(index, glassPane));
        return this;
    }

    public InventoryBuilder fillOuterLine(){
        int rows = size / 9;
        Range range = Range.allHardInRow(0, 8, 0);
        Range rangeLast = Range.allHardInRow(0, 8, rows - 1);
        IntegerBox box = Box.create().addRange(range).addRange(rangeLast);
        fillWithPlaceholder(box);
        Box rowsBox = Box.create();
        IntStream.range(1, rows).forEach(row -> rowsBox.addRange(Range.inRow(row, 0, 8)));
        fillWithPlaceholder(rowsBox);
        return this;
    }

    public InventoryBuilder backItem(int row, int slot){
        row--;
        inv.setItem(row * 9 + (slot - 1), CoreItem.create(Items.BACK.clone()));
        return this;
    }

    public <T> InventoryBuilder bindPaging(CorePlayer sender, PageBinder<T> binder, boolean enableClicker){
        Page<T> current = binder.getFetchPage().get();
        if(current.isEmpty() && current.isLast() && current.hasPrevious()) sender.inventories().pageBefore();
        current = binder.getFetchPage().get();
        InventoryPlaceholder.clear(inv, binder.getBoxing());
        current.forEach(item -> inv.addItem(binder.getMapper().apply(item)));
        if(current.hasNext()) nextPageItem(sender, () -> bindPaging(sender, binder, enableClicker));
        if(current.hasPrevious()) previousPageItem(sender, () -> bindPaging(sender, binder, enableClicker));
        if(enableClicker)
            sender.inventories().onSlotClick(inv -> bindPaging(sender, binder, true), binder.getBoxing());

        Page<T> finalCurrent = current;
        ArematicsExecutor.syncDelayed(() -> {
            if(!finalCurrent.hasNext()) fillWithPlaceholder(Range.custom(this.size - 1));
            if(!finalCurrent.hasPrevious()) fillWithPlaceholder(Range.custom(this.size - 9));
            sender.getPlayer().updateInventory();
        }, 50, TimeUnit.MILLISECONDS);
        return this;
    }

    private void nextPageItem(CorePlayer sender, Runnable run){
        CoreItem item = CoreItem.generate(Material.ARROW)
                .setName("§cNext Page");
        inv.setItem(this.size - 1, item);
        sender.inventories().unregisterItemListeners(item);
        sender.inventories().registerItemClick(item, () -> {
            sender.inventories().nextPage();
            run.run();
        });
    }

    private void previousPageItem(CorePlayer sender, Runnable run){
        CoreItem item = CoreItem.generate(Material.ARROW)
                .setName("§cPage Before");
        inv.setItem(this.size - 9, item);
        sender.inventories().unregisterItemListeners(item);
        sender.inventories().registerItemClick(item, () -> {
            sender.inventories().pageBefore();
            run.run();
        });
    }

    public InventoryBuilder addItem(ItemStack item, int row, int slot){
        row--;
        inv.setItem(row * 9 + (slot - 1), item);
        return this;
    }

    public Inventory fetchInventory(){
        return this.inv;
    }
}
