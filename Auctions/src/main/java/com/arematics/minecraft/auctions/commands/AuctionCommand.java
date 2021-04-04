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
import com.arematics.minecraft.core.utils.ArematicsExecutor;
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
import java.text.DecimalFormat;
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
        InventoryBuilder.create("Auctions", 3)
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
    public void searchMarket(CorePlayer player) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        Supplier<Page<Auction>> auctions = () -> auctionService.findAllByFilter(() ->
                playerAuctionSettingsService.findOrCreateDefault(player.getUUID()), 0);
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
        InventoryBuilder builder = InventoryBuilder.create("Auction Search", 6)
                .openBlocked(player)
                .fillOuterLine()
                .bindPaging(player, binder, false)
                .backItem(6, 5)
                .addItem(categories, 2, 1)
                .addItem(type, 6, 6)
                .addItem(sort,6,7);
        Runnable run = () -> builder.bindPaging(player, binder, false);
        player.inventories()
                .addRefresher(run)
                .onSlotClick((inv, item) -> {
                    try{
                        Auction auction = auctionService.findById(Auction.readAuctionIdFromItem(item));
                        player.dispatchCommand("auction " + auction.getAuctionId());
                    }catch (Exception ignore){}
                }, range)
                .enableRefreshTask();
        player.inventories().registerItemClick(categories, item -> item.bindEnumLore(updateContent(player, ps ->
                ps.setItemCategory(EnumUtils.getNext(ps.getItemCategory())), run).getItemCategory()));
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
        OwnAuctionFilter auctionFilter = OwnAuctionFilter.ALL;
        EndTimeFilter endTimeFilter = EndTimeFilter.ENDING_SOON;
        CoreItem newAuction = server.items().generateNoModifier(Material.WORKBENCH)
                .setName("§aCreate new auction")
                .addToLore("§8Create a new auction")
                .addToLore("§8Or click a item in your inventory", "§8to create a new auction for this item");
        CoreItem endingFilter = server.items().generateNoModifier(Material.PAPER).setName("§bAuction Ending Filter").bindEnumLore(auctionFilter);
        CoreItem endingSort = server.items().generateNoModifier(Material.DROPPER).setName("§bEnd Time Sort").bindEnumLore(endTimeFilter);
        Supplier<Page<Auction>> auctions = () -> auctionService.findAllOwnByFilter(player.getUUID(),
                () -> player.inventories().getEnumOrDefault(endTimeFilter),
                () -> player.inventories().getEnumOrDefault(auctionFilter),
                0);
        Range range = Range.allHardInRows(1, 7, 1, 2);
        PageBinder<Auction> binder = PageBinder.of(auctions, range, server);
        InventoryBuilder builder = InventoryBuilder.create("Your Auctions", 4)
                .openBlocked(player)
                .fillOuterLine()
                .bindPaging(player, binder, false)
                .addItem(newAuction, 2, 1)
                .addItem(endingFilter, 1, 4)
                .addItem(endingSort,1,5)
                .backItem(4, 5);
        player.inventories()
                .addRefresher(() -> builder.bindPaging(player, binder, false))
                .enableRefreshTask()
                .onItemInOwnInvClick(clicked -> new AuctionCreator(player, clicked, server))
                .registerItemClick(newAuction, () -> new AuctionCreator(player, null, server))
                .onSlotClick((inv, item) -> ArematicsExecutor.runAsync(() -> {
                    try{
                        Auction auction = auctionService.findById(Auction.readAuctionIdFromItem(item));
                        player.dispatchCommand("auction collect " + auction.getAuctionId());
                    }catch (Exception ignore){}
                }), range)
                .registerEnumItemClickWithRefresh(endingFilter, auctionFilter)
                .registerEnumItemClickWithRefresh(endingSort, endTimeFilter);
    }

    @SubCommand("{auction}")
    public void openAuctionToBuyOrEdit(CorePlayer sender, Auction auction) {
        if(sender.getUUID().equals(auction.getCreator())) openAuctionRemove(sender, auction);
        else openAuctionBuyInventory(sender, auction);
    }

    @SubCommand("buy {auction}")
    public void instantBuyAuction(CorePlayer sender, Auction auction) {
        if(sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("You are not allowed to buy your own auction");
        if(auction.getInstantSell() == 0)
            throw new CommandProcessException("Auction is not for instant sell");

        if(auction.ended()) {
            sender.warn("Auction arleady ended or sold").handle();
            searchMarket(sender);
        }else {
            double amount = auction.getInstantSell();
            if(sender.getMoney() < amount){
                sender.warn("Not enough coins to effort this").handle();
                searchMarket(sender);
            }else{
                CoreItem item = auction.getSell()[0];
                boolean success = server.currencyController()
                        .createEvent(sender)
                        .setAmount(amount)
                        .setEventType(CurrencyEventType.TRANSFER)
                        .setTarget("auction-instant-buy")
                        .onSuccess(() -> sender.removeMoney(amount));
                if(success){
                    server.items().giveItemTo(sender, item);
                    auction.setSold(true);
                    auctionService.save(auction);
                    sender.info("Auction bought").handle();
                    sender.getPlayer().closeInventory();
                }else
                    sender.failure("Auction could not be bought").handle();
            }
        }
    }

    @SubCommand("bid {auction}")
    public void bidOnAuction(CorePlayer sender, Auction auction) {
        if(auction.getStartPrice() == 0)
            throw new CommandProcessException("Auction is not for bid");
        if(sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("You are not allowed to bid on your own auction");
        if(auction.ended()) {
            sender.warn("Auction already ended or sold").handle();
            searchMarket(sender);
        }else {
            BidId id = new BidId(auction.getAuctionId(), sender.getUUID());
            Bid bid = null;
            try{
                bid = bidService.findById(id);
                auction.getBids().remove(bid);
            }catch (Exception ignore){}
            double min = Math.max(auction.getStartPrice(), auction.highestBidPrice())
                    * 1.05 - (bid != null ? bid.getAmount() : 0);
            try {
                String result = ArematicsExecutor.awaitAnvilResult("bid: ", new DecimalFormat("#0").format(min), sender);
                double value = Double.parseDouble(result);
                if(sender.getMoney() < value)
                    sender.warn("Not enough money").handle();
                else if(value < min)
                    sender.warn("Minimum 5% higher price then current highest bid or start price").handle();
                else{
                    boolean success = server.currencyController()
                            .createEvent(sender)
                            .setAmount(value)
                            .setEventType(CurrencyEventType.TRANSFER)
                            .setTarget("auction-bid")
                            .onSuccess(() -> sender.removeMoney(value));
                    if(success){
                        if(bid == null) bid = new Bid(auction.getAuctionId(), sender.getUUID(), value);
                        else bid.setAmount(value);
                        bidService.save(bid);
                        auction.setEndTime(Timestamp.valueOf(auction.getEndTime().toLocalDateTime()
                                .plusSeconds(20)));
                        sender.info("Bid placed").handle();
                    }else sender.failure("Bid could not be placed").handle();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.failure("Auction bid could not be place").handle();
            }
            auction.getBids().add(bid);
            auctionService.save(auction);
        }
    }

    @SubCommand("collect {auction}")
    public void collectOwnAuction(CorePlayer sender, Auction auction) {
        if(!sender.getUUID().equals(auction.getCreator()))
            throw new CommandProcessException("Not your own auction");
        if(!auction.ended()) {
            sender.dispatchCommand("auction " + auction.getAuctionId());
            return;
        }
        double amount = auction.isSold() ? auction.getInstantSell() : auction.highestBidPrice();
        if(amount == auction.getStartPrice()){
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
        InventoryBuilder builder = InventoryBuilder.create("Buy Auction", 6)
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
        InventoryBuilder.create("Remove Auction", 3)
                .openBlocked(player)
                .fillAll()
                .addItem(cancel, 2, 2)
                .addItem(item, 2, 5)
                .addItem(remove, 2, 8);
        Runnable run = () -> shopOwnSells(player);
        player.inventories().registerItemClick(cancel, run);
        player.inventories().registerItemClick(remove, () -> {
            try{
                server.items().giveItemTo(player, auction.getSell()[0]);
                auctionService.delete(auction);
                player.info("Received not sold item back").handle();
            }catch (Exception ignore){
                player.failure("Auction could not be removed").handle();
            }
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
