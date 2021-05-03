package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.utils.EnumUtils;
import com.arematics.minecraft.data.global.model.EndTimeFilter;
import com.arematics.minecraft.data.mode.model.*;
import com.arematics.minecraft.data.service.AuctionService;
import com.arematics.minecraft.data.service.BidService;
import com.arematics.minecraft.data.service.PlayerAuctionSettingsService;
import org.bukkit.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class AuctionCommand extends CoreCommand {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final PlayerAuctionSettingsService playerAuctionSettingsService;

    @Autowired
    public AuctionCommand(AuctionService auctionService,
                          BidService bidService,
                          PlayerAuctionSettingsService playerAuctionSettingsService){
        super("auction", "market", "markt");
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.playerAuctionSettingsService = playerAuctionSettingsService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        InventoryBuilder.create("Auctions", 3, sender)
                .openBlocked(sender)
                .fillAll()
                .addItem(server.items().generateNoModifier(Material.DIAMOND_BLOCK)
                        .bindCommand("auction search")
                        .setName("§bView All Auctions"), 2,3)
                .addItem(server.items().generateNoModifier(Material.BOOK)
                        .bindCommand("auction bids")
                        .setName("§bYour Bids"), 2,5)
                .addItem(server.items().generateNoModifier(Material.CHEST)
                        .bindCommand("auction sells")
                        .setName("§bManage your auctions"), 2,7);
    }

    @SubCommand("search")
    public void searchMarket(CorePlayer sender) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(sender.getUUID());
        Supplier<Page<Auction>> auctions = () -> auctionService.findAllByFilter(() ->
                playerAuctionSettingsService.findOrCreateDefault(sender.getUUID()), sender.inventories().getPage());
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Auction> binder = PageBinder.of(auctions, range, server);
        CoreItem categories = server.items()
                .generateNoModifier(Material.DIAMOND)
                .setName("§bAuction Categories")
                .bindEnumLore(settings.getItemCategory());
        CoreItem type = server.items()
                .generateNoModifier(Material.PAPER)
                .setName("§bAuction Type")
                .bindEnumLore(settings.getAuctionType());
        CoreItem sort = server.items()
                .generateNoModifier(Material.DROPPER)
                .setName("§bAuction Sort")
                .bindEnumLore(settings.getAuctionSort());
        InventoryBuilder builder = InventoryBuilder.create("Auction Search", 6, sender)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, false)
                .backItem(6, 5)
                .addItem(categories, 2, 1)
                .addItem(type, 6, 6)
                .addItem(sort,6,7);
        Runnable run = () -> builder.bindPaging(sender, binder, false);
        sender.inventories()
                .addRefresher(run)
                .onSlotClick((inv, item) -> {
                    try{
                        Auction auction = auctionService.findById(Auction.readAuctionIdFromItem(item));
                        sender.interact().dispatchCommand("auction " + auction.getAuctionId());
                    }catch (Exception ignore){}
                }, range)
                .enableRefreshTask();
        sender.inventories().registerItemClick(categories, item -> item.bindEnumLore(updateContent(sender, ps ->
                ps.setItemCategory(EnumUtils.getNext(ps.getItemCategory())), run).getItemCategory()));
        sender.inventories().registerItemClick(type, item -> item.bindEnumLore(updateContent(sender, ps ->
                ps.setAuctionType(EnumUtils.getNext(ps.getAuctionType())), run).getAuctionType()));
        sender.inventories().registerItemClick(sort, item -> item.bindEnumLore(updateContent(sender, ps ->
                ps.setAuctionSort(EnumUtils.getNext(ps.getAuctionSort())), run).getAuctionSort()));
    }

    @SubCommand("bids")
    public void showCurrentBids(CorePlayer sender) {
        Supplier<Page<Bid>> bids = () -> bidService.findAllByBidder(sender.getUUID(), sender.inventories().getPage());
        Range range = Range.allHardInRows(1, 7, 1, 2);
        PageBinder<Bid> binder = PageBinder.of(bids, range, this::mapToItem);
        InventoryBuilder builder = InventoryBuilder.create("Your Bids", 4, sender)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, false);
        sender.inventories()
                .addRefresher(() -> builder.bindPaging(sender, binder, false))
                .enableRefreshTask()
                .onSlotClick((inv, item) -> server.schedule().runAsync(() -> {
                    try{
                        Auction auction = auctionService.findById(Auction.readAuctionIdFromItem(item));
                        sender.interact().dispatchCommand("auction bidcollect " + auction.getAuctionId());
                    }catch (Exception ignore){}
                }), range);
    }

    @SubCommand("bidcollect {auction}")
    public void collectAuctionBid(CorePlayer sender, Auction auction) {
        if(!auction.ended())
            throw new CommandProcessException("Auction is in progress. Bid could not be collected yet");
        try{
            BidId id = new BidId(auction.getAuctionId(), sender.getUUID());
            Bid bid = bidService.findById(id);
            if(auction.isSold() || auction.getHighestBidPrice() != bid.getAmount()){
                boolean success = server.currencyController()
                        .createEvent(sender)
                        .setAmount(bid.getAmount())
                        .setEventType(CurrencyEventType.TRANSFER)
                        .setTarget("auction-bid-get-back")
                        .addMoney();
                if(success) {
                    removeBidForAuction(auction, bid);
                    sender.info("You got back " + bid.getAmount() + " coins from your bid").handle();
                } else sender.failure("Something went wrong on collection your bid").handle();
            }else if(auction.getHighestBidPrice() == bid.getAmount()){
                server.items().giveItemTo(sender, auction.getSell()[0]);
                removeBidForAuction(auction, bid);
                sender.info("You received your item").handle();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new CommandProcessException("Could not find bid for you on auction: " + auction.getAuctionId());
        }
    }

    public void removeBidForAuction(Auction auction, Bid bid){
        auction.getBids().remove(bid);
        if(auction.getBids().isEmpty() && auction.ended() && auction.isOwnerCollected()){
            auctionService.delete(auction);
        }else{
            auctionService.save(auction);
        }
        bidService.remove(bid);
    }

    public CoreItem mapToItem(Bid bid){
        Auction auction = auctionService.findById(bid.getAuctionId());
        return auction.mapToItem(server);
    }

    @SubCommand("sells")
    public void shopOwnSells(CorePlayer sender) {
        OwnAuctionFilter auctionFilter = OwnAuctionFilter.ALL;
        EndTimeFilter endTimeFilter = EndTimeFilter.ENDING_SOON;
        CoreItem newAuction = server.items().generateNoModifier(Material.WORKBENCH)
                .setName("§aCreate new auction")
                .addToLore("§8Create a new auction")
                .addToLore("§8Or click a item in your inventory", "§8to create a new auction for this item");
        CoreItem endingFilter = server.items().generateNoModifier(Material.PAPER).setName("§bAuction Ending Filter").bindEnumLore(auctionFilter);
        CoreItem endingSort = server.items().generateNoModifier(Material.DROPPER).setName("§bEnd Time Sort").bindEnumLore(endTimeFilter);
        Supplier<Page<Auction>> auctions = () -> auctionService.findAllOwnByFilter(sender.getUUID(),
                () -> sender.inventories().getEnumOrDefault(endTimeFilter),
                () -> sender.inventories().getEnumOrDefault(auctionFilter),
                sender.inventories().getPage());
        Range range = Range.allHardInRows(1, 7, 1, 2);
        PageBinder<Auction> binder = PageBinder.of(auctions, range, server);
        InventoryBuilder builder = InventoryBuilder.create("Your Auctions", 4, sender)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, false)
                .addItem(newAuction, 2, 1)
                .addItem(endingFilter, 1, 4)
                .addItem(endingSort,1,5)
                .backItem(4, 5);
        sender.inventories()
                .addRefresher(() -> builder.bindPaging(sender, binder, false))
                .enableRefreshTask()
                .onItemInOwnInvClick(clicked -> {
                    new AuctionCreator(sender, clicked, server);
                    return null;
                })
                .registerItemClick(newAuction, () -> new AuctionCreator(sender, null, server))
                .onSlotClick((inv, item) -> server.schedule().runAsync(() -> {
                    try{
                        Auction auction = auctionService.findById(Auction.readAuctionIdFromItem(item));
                        sender.interact().dispatchCommand("auction collect " + auction.getAuctionId());
                    }catch (Exception ignore){}
                }), range)
                .registerEnumItemClickWithRefresh(endingFilter, auctionFilter)
                .registerEnumItemClickWithRefresh(endingSort, endTimeFilter);
    }

    @SubCommand("{auction}")
    public void openAuctionToBuyOrEdit(CorePlayer sender, Auction auction) {
        if(sender.getUUID().equals(auction.getCreator())) {
            if(auction.isOwnerCollected()) throw new CommandProcessException("Auction has been collected already");
            openAuctionRemove(sender, auction);
        }else{
            if(auction.ended()) throw new CommandProcessException("Auction already ended or sold");
            openAuctionBuyInventory(sender, auction);
        }
    }

    @SubCommand("buy {auction}")
    public void instantBuyAuction(CorePlayer sender, Auction auction) {
        if (sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("You are not allowed to buy your own auction");
        if (auction.getInstantSell() == 0)
            throw new CommandProcessException("Auction is not for instant sell");
        if (auction.ended())
            throw new CommandProcessException("Auction already ended or sold");

        double amount = auction.getInstantSell();
        if (sender.getMoney() < amount) {
            sender.warn("Not enough coins to effort this").handle();
            searchMarket(sender);
        } else {
            CoreItem item = auction.getSell()[0];
            boolean success = server.currencyController()
                    .createEvent(sender)
                    .setAmount(amount)
                    .setEventType(CurrencyEventType.TRANSFER)
                    .setTarget("auction-instant-buy")
                    .onSuccess(() -> sender.removeMoney(amount));
            if (success) {
                server.items().giveItemTo(sender, item);
                auction.setSold(true);
                auctionService.save(auction);
                sender.info("Auction bought").handle();
                sender.getPlayer().closeInventory();
            } else
                sender.failure("Auction could not be bought").handle();
        }
    }

    @SubCommand("bid {auction}")
    public void bidOnAuction(CorePlayer sender, Auction auction) {
        if(auction.getStartPrice() == 0)
            throw new CommandProcessException("Auction is not for bid");
        if(sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("You are not allowed to bid on your own auction");
        if (auction.ended())
            throw new CommandProcessException("Auction already ended or sold");
        BidId id = new BidId(auction.getAuctionId(), sender.getUUID());
        Bid bid = null;
        try{
            bid = bidService.findById(id);
        }catch (Exception ignore){}
        double start = (Math.max(auction.getStartPrice(), auction.getHighestBidPrice()) * 1.05);
        try {
            double value = server.schedule().awaitNumberResult("Set Bid Price ", start, start, sender).doubleValue();
            if(sender.getMoney() < value)
                throw new CommandProcessException("Not enough money");
            if(value < start)
                throw new CommandProcessException("Minimum 5% higher price then current highest bid or start price");
            boolean success = server.currencyController()
                    .createEvent(sender)
                    .setAmount(value - (bid != null ? bid.getAmount() : 0))
                    .setEventType(CurrencyEventType.TRANSFER)
                    .setTarget("auction-bid")
                    .removeMoney();
            if(success){
                if(bid == null) bid = new Bid(auction.getAuctionId(), sender.getUUID(), value);
                else{
                    auction.getBids().remove(bid);
                    bid.setAmount(value);
                }
                auction.getBids().add(bid);
                bidService.save(bid);
                auction.setHighestBidPrice(value);
                auction.setLastHighestBidder(sender.getName());
                auctionService.save(auction);
                auction.setEndTime(Timestamp.valueOf(auction.getEndTime().toLocalDateTime()
                        .plusSeconds(20)));
                sender.info("Bid placed").handle();
            }else sender.failure("Bid could not be placed").handle();
        } catch (InterruptedException e) {
            sender.failure("Bid Amount input has been interrupted").handle();
        }
    }

    @SubCommand("collect {auction}")
    public void collectOwnAuction(CorePlayer sender, Auction auction) {
        if(!sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("Not your own auction");
        if(auction.isOwnerCollected())
            throw new CommandProcessException("Auction has been collected already");
        if(!auction.ended()){
            sender.interact().dispatchCommand("auction " + auction.getAuctionId());
            return;
        }
        collectAuction(sender, auction);
    }

    private void collectAuction(CorePlayer sender, Auction auction){
        double amount = auction.isSold() ? auction.getInstantSell() : auction.getHighestBidPrice();
        if(!auction.ended()){
            server.items().giveItemTo(sender, auction.getSell()[0]);
            sender.info("Received not sold item back").handle();
        }else{
            boolean success = server.currencyController()
                    .createEvent(sender)
                    .setAmount(amount)
                    .setEventType(CurrencyEventType.TRANSFER)
                    .setTarget("auction-sell")
                    .addMoney();
            if(success)
                sender.info("Auction collected successful").handle();
            else
                throw new CommandProcessException("Auction could not be collected");
        }
        if(!auction.getBids().isEmpty()){
            boolean sold = !auction.ended();
            auction.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
            auction.setOwnerCollected(true);
            auction.setSold(sold);
            auctionService.save(auction);
        }else
            auctionService.delete(auction);
    }

    private void openAuctionBuyInventory(CorePlayer player, Auction auction){
        CoreItem item = auction.getSell()[0];
        CoreItem back = server.items().create(Items.BACK.clone())
                .removeMeta(CoreItem.BINDED_COMMAND);
        CoreItem bidItem = server.items().generateNoModifier(Material.SIGN)
                .bindCommand("auction bid " + auction.getAuctionId())
                .setName("§aPlace a bid")
                .addToLore("§8Keep auction and do not stop it");
        CoreItem instantBuy = server.items().generateNoModifier(Material.GOLD_INGOT)
                .bindCommand("auction buy " + auction.getAuctionId())
                .setName("§aInstant Buy Auction")
                .addToLore("§8Instant Buy Price: §e" + auction.getInstantSell() + " Coins")
                .addToLore("§8Directly buy this auction for the given price");
        InventoryBuilder builder = InventoryBuilder.create("Buy Auction", 6, player)
                .openBlocked(player)
                .fillAll()
                .addItem(item, 2, 5)
                .addItem(back, 6, 5);
        if(auction.getStartPrice() != 0)
            builder.addItem(bidItem, 4, 3);
        if(auction.getInstantSell() != 0)
            builder.addItem(instantBuy, 4, 7);
        player.inventories().registerItemClick(back, () -> this.searchMarket(player));


    }

    private void openAuctionRemove(CorePlayer player, Auction auction){
        CoreItem item = auction.getSell()[0];
        CoreItem cancel = server.items().generateNoModifier(Material.EMERALD_BLOCK)
                .setName("§aKeep auction")
                .addToLore("§8Keep auction and do not stop it");
        CoreItem remove = server.items().generateNoModifier(Material.REDSTONE_BLOCK)
                .setName("§cRemove auction")
                .addToLore("§8Remove auction earlier", " ", "§cYou do not get back your auction creation costs");
        InventoryBuilder.create("Remove Auction", 3, player)
                .openBlocked(player)
                .fillAll()
                .addItem(cancel, 2, 2)
                .addItem(item, 2, 5)
                .addItem(remove, 2, 8);
        Runnable run = () -> shopOwnSells(player);
        player.inventories().registerItemClick(cancel, run);
        player.inventories().registerItemClick(remove, () -> {
            collectAuction(player, auction);
            run.run();
        });
    }

    private PlayerAuctionSettings updateContent(CorePlayer player, Consumer<PlayerAuctionSettings> update, Runnable runnable){
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        update.accept(settings);
        settings = playerAuctionSettingsService.save(settings);
        runnable.run();
        return settings;
    }
}
