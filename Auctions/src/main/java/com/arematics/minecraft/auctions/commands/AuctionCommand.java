package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.utils.EnumUtils;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.PlayerAuctionSettings;
import com.arematics.minecraft.data.service.AuctionCategoryService;
import com.arematics.minecraft.data.service.AuctionService;
import com.arematics.minecraft.data.service.PlayerAuctionSettingsService;
import org.bukkit.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
        InventoryBuilder.create("Auctions", 3)
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
        Supplier<Page<Auction>> auctions = () -> auctionService.findAllByFilter(() ->
                playerAuctionSettingsService.findOrCreateDefault(player.getUUID()), 0);
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Auction> binder = PageBinder.of(auctions, range);
        CoreItem type = CoreItem.generate(Material.PAPER)
                .setName("§bAuction Type")
                .bindEnumLore(settings.getAuctionType());
        CoreItem sort = CoreItem.generate(Material.DROPPER)
                .setName("§bAuction Sort")
                .bindEnumLore(settings.getAuctionSort());
        InventoryBuilder builder = InventoryBuilder.create("Auction Search", 6)
                .openBlocked(player)
                .fillOuterLine()
                .bindPaging(player, binder, false)
                .backItem(6, 5)
                .addItem(type, 6, 6)
                .addItem(sort,6,7);
        Runnable run = () -> builder.bindPaging(player, binder, false);
        player.inventories().registerItemClick(type, item -> item.bindEnumLore(updateContent(player, ps ->
                ps.setAuctionType(EnumUtils.getNext(ps.getAuctionType())), run).getAuctionType()));
        player.inventories().registerItemClick(sort, item -> item.bindEnumLore(updateContent(player, ps ->
                ps.setAuctionSort(EnumUtils.getNext(ps.getAuctionSort())), run).getAuctionSort()));
    }
    
    @SubCommand("bids")
    public void showCurrentBids(CorePlayer player) {
        throw new CommandProcessException("Command not working at the moment");
    }

    @SubCommand("sells")
    public void shopOwnSells(CorePlayer player) {
        InventoryBuilder.create("Your Auctions", 4)
                .openBlocked(player)
                .fillOuterLine()
                .backItem(4, 5);
        player.inventories().onItemInOwnInvClick(clicked -> createNewSell(player, clicked));
    }

    public void createNewSell(CorePlayer player, CoreItem item) {
        CoreItem back = CoreItem.create(Items.BACK.clone())
                .bindCommand("auction sells");
        InventoryBuilder.create("New Auction", 6)
                .openBlocked(player)
                .fillAll()
                .addItem(back, 6, 5)
                .addItem(item, 2, 5);
    }

    private PlayerAuctionSettings updateContent(CorePlayer player, Consumer<PlayerAuctionSettings> update, Runnable runnable){
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        update.accept(settings);
        settings = playerAuctionSettingsService.save(settings);
        runnable.run();
        return settings;
    }
}
