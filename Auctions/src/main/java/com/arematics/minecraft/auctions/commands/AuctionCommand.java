package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.*;
import com.arematics.minecraft.data.service.AuctionCategoryService;
import com.arematics.minecraft.data.service.AuctionService;
import com.arematics.minecraft.data.service.PlayerAuctionSettingsService;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AuctionCommand extends CoreCommand {

    private final Server server;
    private final AuctionService auctionService;
    private final AuctionCategoryService auctionCategoryService;
    private final PlayerAuctionSettingsService playerAuctionSettingsService;

    private final CoreItem backToMenu;

    @Autowired
    public AuctionCommand(Server server,
                          AuctionService auctionService,
                          AuctionCategoryService auctionCategoryService,
                          PlayerAuctionSettingsService playerAuctionSettingsService){
        super("auction", "market", "markt");
        this.server = server;
        this.auctionService = auctionService;
        this.auctionCategoryService = auctionCategoryService;
        this.playerAuctionSettingsService = playerAuctionSettingsService;
        this.backToMenu = CoreItem.generate(Material.COMPASS)
                .bindCommand("auction")
                .setName("§bBack to menu");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        InventoryBuilder.create("§8Auctions", 3)
                .openBlocked(sender)
                .fillAll()
                .addItem(CoreItem.generate(Material.DIAMOND_BLOCK)
                        .bindCommand("auction search")
                        .setName("§bView All Auctions"), 2,3)
                .addItem(CoreItem.generate(Material.BOOK)
                        .bindCommand("auction bids")
                        .setName("§bYour Bids"), 2,5)
                .addItem(CoreItem.generate(Material.CHEST)
                        .bindCommand("auction sells")
                        .setName("§bManage your auctions"), 2,7);
    }

    @SubCommand("search")
    public void searchMarket(CorePlayer player) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        InventoryBuilder builder = InventoryBuilder.create("§8Auction Search", 6)
                .openBlocked(player)
                .fillOuterLine()
                .backItem(6, 5);
        CoreItem type = CoreItem.generate(Material.PAPER)
                .bindCommand("auction next type")
                .setName("§bAuction Type")
                .bindEnumLore(settings.getAuctionType());
        player.inventories().registerItemClick(type, item -> item.bindEnumLore(updateContent(player).getAuctionType()));
        builder.addItem(type, 6, 6);
        CoreItem sort = CoreItem.generate(Material.DROPPER)
                .bindCommand("auction next sort")
                .setName("§bAuction Sort")
                .bindEnumLore(settings.getAuctionSort());
        player.inventories().registerItemClick(sort, item -> item.bindEnumLore(updateContent(player).getAuctionSort()));
        builder.addItem(sort, 6, 7);
        updateItems(builder.fetchInventory(), settings);
    }
    
    @SubCommand("bids")
    public void showCurrentBids(CorePlayer player) {
        throw new CommandProcessException("Command not working at the moment");
    }

    @SubCommand("sells")
    public void shopOwnSells(CorePlayer player) {
        Inventory inv = Bukkit.createInventory(null, 5*9, "§8Your Auctions");
        player.inventories().openTotalBlockedInventory(inv);
        InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
        inv.setItem(4 * 9 + 4, CoreItem.create(Items.BACK.clone()));
        player.inventories().onEmptySlotClick(clicked -> player.dispatchCommand("auction sells createNew"));
    }

    @SubCommand("sells createNew")
    public void createNewSell(CorePlayer player) {
        Inventory inv = Bukkit.createInventory(null, 3*9, "§8New Auction");
        player.inventories().openLowerEnabledInventory(inv);
        InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
        inv.setItem(3 * 9 + 4, CoreItem.create(Items.BACK.clone()));
    }

    @SubCommand("next type")
    public void selectNextType(CorePlayer player) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        AuctionType[] types = AuctionType.values();
        int index = settings.getAuctionType().ordinal();
        if(index == types.length - 1) index = 0;
        else index += 1;
        settings.setAuctionType(types[index]);
        playerAuctionSettingsService.save(settings);
    }

    @SubCommand("next sort")
    public void selectNextSort(CorePlayer player) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        AuctionSort[] types = AuctionSort.values();
        int index = settings.getAuctionSort().ordinal();
        if(index == types.length - 1) index = 0;
        else index += 1;
        settings.setAuctionSort(types[index]);
        playerAuctionSettingsService.save(settings);
    }

    private PlayerAuctionSettings updateContent(CorePlayer player){
        InventoryView view = player.inventories().getView();
        AuctionCategory category = auctionCategoryService.findById("stone");
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        settings.setCategory(category);
        settings = playerAuctionSettingsService.save(settings);
        updateItems(view.getTopInventory(), settings);
        return settings;
    }

    private void updateItems(Inventory inv, PlayerAuctionSettings settings){
        CoreItem item = CoreItem.generate(Material.REDSTONE_BLOCK)
                .disableClick()
                .setName("§cError")
                .addToLore("    §8> Could not find items for this search");
        Page<Auction> auctions = auctionService.findAllByFilter(settings, 0);
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        InventoryPlaceholder.clear(inv, range);
        if(auctions.getContent().isEmpty()) InventoryPlaceholder.fillFree(inv, item);
        auctions.forEach(auction -> inv.addItem(auction.getSell()[0]
                .addToLore("§7Auction ID: §c" + auction.getAuctionId())
                .addToLore("§7Start Bid Price: §c" + auction.getStartPrice())
                .addToLore("§7Highest Bid Price: §c" + Collections.max(auction.getBids()).getAmount())
                .addToLore("§7Ending in: §c" + TimeUtils.fetchEndDate(auction.getEndTime()))));
    }
}
