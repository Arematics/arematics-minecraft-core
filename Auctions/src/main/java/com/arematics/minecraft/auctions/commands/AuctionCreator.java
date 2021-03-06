package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionType;
import com.arematics.minecraft.data.service.AuctionService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.joda.time.Period;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionCreator {

    private final CoreItem noItem;

    private final CorePlayer player;
    private final double moneyExists;
    private CoreItem item;

    private Period time = Period.hours(6);
    private double bitStart = 25000;
    private double instantBuy;

    private double sellPrice;

    private final Server server;

    private InventoryBuilder current;
    private final CloseListener closeListener;

    public AuctionCreator(CorePlayer player, CoreItem item, Server server){
        this.player = player;
        this.server = server;
        this.moneyExists = server.moneyStatistics().getMoneyExists();
        this.closeListener = new CloseListener();
        Bukkit.getPluginManager().registerEvents(closeListener, Boots.getBoot(CoreBoot.class));
        this.noItem = server.items().generateNoModifier(Material.WOOD_BUTTON)
                .setName("§cNo Item selected")
                .addToLore("§8Select a item in your inventory");
        if(item == null) this.item = noItem;
        else this.item = server.items().create(item);
        openBaseInventory();
    }

    public void openBaseInventory(){
        double bitPricing = getDynamicBalance(this.bitStart, 500, 100);
        double instantPricing = getDynamicBalance(this.instantBuy, 300, 50);
        double timePrice = (time.toStandardHours().getHours() * 100);
        this.sellPrice = bitPricing + instantPricing + timePrice;
        CoreItem back = server.items().create(Items.BACK.clone())
                .bindCommand("auction sells");
        CoreItem timeNext = server.items().generateNoModifier(Material.WATCH)
                .setName("§aChoose Auction Time")
                .addToLore("§8Current Time: §e" + TimeUtils.toString(time))
                .addToLore("§8Current Costs: §e" + timePrice + " Coins")
                .addToLore("§8Click to choose auction time")
                .addToLore("§8Higher time costs more");
        CoreItem coinsNext = server.items().generateNoModifier(Material.GOLD_INGOT)
                .setName("§aChoose Start Price")
                .addToLore("§8Current Bit Start: §e" + (bitStart == 0 ? "Not Enabled" : bitStart + " Coins"))
                .addToLore("§8Current Costs: §e" + bitPricing + " Coins")
                .addToLore("§8Click to choose start price")
                .addToLore("§8Higher start price costs more");
        CoreItem instant = server.items().generateNoModifier(Material.GOLD_BLOCK)
                .setName("§aChoose Instant Buy Price")
                .addToLore("§8Current Price: §e" + (instantBuy == 0 ? "Not Enabled" : instantBuy + " Coins"))
                .addToLore("§8Current Costs: §e" + instantPricing + " Coins")
                .addToLore("§8Click to choose instant buy price")
                .addToLore("§8Higher instant buy price costs more");
        AtomicReference<CoreItem> createAuction = new AtomicReference<>(server.items().generateNoModifier(Material.DIAMOND)
                .setName(item.isSimilar(noItem) ? "§cNo item selected" : "§aCreate auction")
                .addToLore("§8Current Costs: §e" + sellPrice + " Coins")
                .addToLore(item.isSimilar(noItem) ? "§cPublish auction not possible" : "§aPublish auction"));
        current = InventoryBuilder.create("New Auction", 6, player)
                .openBlocked(player)
                .fillAll()
                .addItem(item, 2, 5)
                .addItem(createAuction.get(), 4, 2)
                .addItem(coinsNext, 4, 5)
                .addItem(instant, 4, 6)
                .addItem(timeNext, 4, 8)
                .addItem(back, 6, 5);
        player.inventories().registerItemClick(timeNext, this::openAuctionTimeChoose);
        if(!item.isSimilar(noItem))
            player.inventories().registerItemClick(createAuction.get(), () -> server.schedule().runAsync(this::createAuction));
        player.inventories().onItemInOwnInvClick(clicked -> {
            if(!this.item.isSimilar(noItem)){
                server.items().giveItemTo(player, item);
            }
            this.item = server.items().create(clicked);
            current.addItem(item, 2, 5);
            player.inventories().unregisterItemListeners(createAuction.get());
            createAuction.set(server.items().generateNoModifier(Material.DIAMOND)
                    .setName(item.isSimilar(noItem) ? "§cNo item selected" : "§aCreate auction")
                    .addToLore("§8Current Costs: §e" + sellPrice + " Coins")
                    .addToLore("§aPublish auction"));
            current.addItem(createAuction.get(), 4, 2);
            player.inventories().registerItemClick(createAuction.get(), () ->
                    server.schedule().runAsync(this::createAuction));
            return null;
        });
        player.inventories().registerItemClick(coinsNext, () -> server.schedule().runAsync(() -> {
            try{
                this.bitStart = server.schedule()
                        .awaitNumberResult("Set Start Price ", 1, bitStart, player)
                        .doubleValue();
            }catch (Exception ignore){}
            this.openBaseInventory();
        }));
        player.inventories().registerItemClick(instant, () -> server.schedule().runAsync(() -> {
            try{
                this.instantBuy = server.schedule()
                        .awaitNumberResult("Set Instant Buy Price ", 1, instantBuy, player)
                        .doubleValue();
            }catch (Exception ignore){}
            this.openBaseInventory();
        }));
    }

    public void openAuctionTimeChoose(){
        CoreItem back = server.items().create(Items.BACK.clone())
                .removeMeta(CoreItem.BINDED_COMMAND);
        CoreItem timeOneHour = timeItem(Period.hours(1));
        CoreItem timeSixHour = timeItem(Period.hours(6));
        CoreItem timeTwelveHour = timeItem(Period.hours(12));
        CoreItem timeOneDay = timeItem(Period.days(1));
        CoreItem timeThreeDays = timeItem(Period.days(3));
        CoreItem custom = server.items().generateNoModifier(Material.SIGN)
                .setName("§aSet Custom Auction Time");
        current = InventoryBuilder.create("Auction Time", 3, player)
                .openBlocked(player)
                .fillAll()
                .addItem(timeOneHour, 2, 2)
                .addItem(timeSixHour, 2, 3)
                .addItem(timeTwelveHour, 2, 4)
                .addItem(timeOneDay, 2, 5)
                .addItem(timeThreeDays, 2, 6)
                .addItem(custom, 2, 8)
                .addItem(back, 3, 5);
        player.inventories().registerItemClick(back, this::openBaseInventory);
        player.inventories().registerItemClick(timeOneHour, () -> onItemClick(Period.hours(1)));
        player.inventories().registerItemClick(timeSixHour, () -> onItemClick(Period.hours(6)));
        player.inventories().registerItemClick(timeTwelveHour, () -> onItemClick(Period.hours(12)));
        player.inventories().registerItemClick(timeOneDay, () -> onItemClick(Period.days(1)));
        player.inventories().registerItemClick(timeThreeDays, () -> onItemClick(Period.days(3)));
        player.inventories().registerItemClick(custom, () -> server.schedule().runAsync(() -> {
            try {
                int hours = server.schedule().awaitNumberResult("Choose hours ", 1, 7*24, 1, player).intValue();
                if(hours > (7*24)) {
                    player.warn("Maximum of " + (7*24) + " hours").handle();
                    throw new Exception("Not okay");
                }
                this.time = Period.hours(hours).normalizedStandard();
            } catch (Exception ignore) {}
            this.openBaseInventory();
        }));
    }

    private void onItemClick(Period period){
        this.time = period;
        this.openBaseInventory();
    }

    private void createAuction(){
        if(player.getMoney() < this.sellPrice) {
            player.warn("Not enough money to effort this").handle();
            return;
        }
        Server server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
        boolean success = server.currencyController()
                .createEvent(player)
                .setAmount(this.sellPrice)
                .setEventType(CurrencyEventType.WASTE)
                .setTarget("auction-create")
                .onSuccess(() -> player.removeMoney(this.sellPrice));
        if(!success) {
            player.warn("Money transfer could not be handled").handle();
            return;
        }
        if(item.isSimilar(noItem)){
            player.warn("Please select a item for your auction").handle();
        }else{
            AuctionService auctionService = Boots.getBoot(CoreBoot.class).getContext().getBean(AuctionService.class);
            AuctionType type = bitStart == 0 ? AuctionType.INSTANT_BUY : instantBuy == 0 ? AuctionType.BID : AuctionType.ALL;
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(time.toStandardHours().getHours()));
            Auction auction = new Auction(null, player.getUUID(), bitStart, instantBuy, new CoreItem[]{item},
                    item.readCategory(), type, timestamp, false, false, false, "Nobody", bitStart, new HashSet<>());
            server.schedule().runAsync(() -> auctionService.save(auction));
            this.item = noItem;
            player.getPlayer().closeInventory();
            player.info("Auction created").handle();
        }
    }

    private CoreItem timeItem(Period period){
        CoreItem item = server.items().generateNoModifier(Material.WATCH)
                .setName("§aTime: " + TimeUtils.toString(period))
                .addToLore("§8Set Auction time to §e" + TimeUtils.toString(period))
                .addToLore("§8Costs: §e" + (period.toStandardHours().getHours() * 100) + " Coins");
        if(time.toStandardHours().equals(period.toStandardHours())) return item.setGlow();
        return item;
    }

    private double getDynamicBalance(double value, double highCap, double downCap){
        return value / Math.max(downCap, highCap - (this.moneyExists / modifier()));
    }

    private double modifier(){
        if(moneyExists <= 10000000){
            return 100000;
        }else if (moneyExists <= 100000000){
            return 95000;
        }else if (moneyExists <= 1000000000){
            return 90000;
        }
        return 87000;
    }

    public void closeInventory(){
        if(this.item != null && !this.item.isSimilar(noItem)){
            server.items().giveItemTo(player, item);
            this.item = noItem;
        }
        HandlerList.unregisterAll(closeListener);
    }

    private class CloseListener implements Listener {

        @EventHandler
        public void onClose(InventoryCloseEvent event){
            if(event.getPlayer().equals(player.getPlayer()) &&
                    event.getView().getTopInventory().equals(current.fetchInventory()))
                closeInventory();
        }
    }
}
