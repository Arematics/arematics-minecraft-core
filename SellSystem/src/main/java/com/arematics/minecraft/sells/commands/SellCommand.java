package com.arematics.minecraft.sells.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.data.mode.model.ItemPrice;
import com.arematics.minecraft.data.service.ItemPriceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SellCommand extends CoreCommand {

    private final ItemPriceService itemPriceService;

    @Autowired
    public SellCommand(ItemPriceService itemPriceService){
        super("sell");
        this.itemPriceService = itemPriceService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        new SellGUI(sender, server, itemPriceService);
    }

    @SubCommand("list")
    public void listSells(CorePlayer sender) {
        Supplier<Page<ItemPrice>> fetchPrice = () -> itemPriceService.fetchPrices(sender.inventories().getPage(),
                () -> sender.inventories().getEnumOrDefault(SellListFilter.HIGHEST_PRICE));
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<ItemPrice> binder = PageBinder.of(fetchPrice, range, server);
        CoreItem modeItem = server.items().generateNoModifier(Material.HOPPER)
                .setName("§cQuery Mode")
                .bindEnumLore(SellListFilter.HIGHEST_PRICE);
        CoreItem sellItems = server.items().generateNoModifier(Material.GOLD_INGOT)
                .bindCommand("sell")
                .setName("§aSell Items");
        InventoryBuilder builder = InventoryBuilder.create("Item Prices", 6, sender)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, false)
                .addItem(sellItems, 6, 5)
                .addItem(modeItem, 6, 3);
        sender.inventories()
                .addRefresher(() -> builder.bindPaging(sender, binder, false))
                .registerEnumItemClickWithRefresh(modeItem, SellListFilter.HIGHEST_PRICE);
    }


    @Getter
    @RequiredArgsConstructor
    public enum SellListFilter{
        HIGHEST_PRICE(Sort.by("price").descending()),
        LOWEST_PRICE(Sort.by("price"));

        private final Sort sort;
    }
}
