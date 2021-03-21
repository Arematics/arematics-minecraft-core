package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionCategory;
import com.arematics.minecraft.data.mode.model.AuctionType;
import com.arematics.minecraft.data.service.AuctionCategoryService;
import com.arematics.minecraft.data.service.AuctionService;
import org.bukkit.Material;
import org.joda.time.Period;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionCreator {

    private static final CoreItem noItem = CoreItem.generate(Material.WOOD_BUTTON)
            .setName("§cNo Item selected")
            .addToLore("§8Select a item in your inventory");

    private final CorePlayer player;
    private CoreItem item;

    private Period time = Period.hours(6);
    private double bitStart = 25000;
    private double instantBuy;

    public AuctionCreator(CorePlayer player, CoreItem item){
        this.player = player;
        if(item == null) this.item = noItem;
        else this.item = item;
        openBaseInventory();
    }

    public void openBaseInventory(){
        CoreItem back = CoreItem.create(Items.BACK.clone())
                .bindCommand("auction sells");
        CoreItem timeNext = CoreItem.generate(Material.WATCH)
                .setName("§aChoose Auction Time")
                .addToLore("§8Current Time: §e" + TimeUtils.toString(time))
                .addToLore("§8Current Costs: §e" + (time.toStandardHours().getHours() * 100) + " Coins")
                .addToLore("§8Click to choose auction time")
                .addToLore("§8Higher time costs more");
        CoreItem coinsNext = CoreItem.generate(Material.GOLD_INGOT)
                .setName("§aChoose Start Price")
                .addToLore("§8Current Bit Start: §e" + (bitStart == 0 ? "Not Enabled" : bitStart + " Coins"))
                .addToLore("§8Current Costs: §e" + (bitStart / 500) + " Coins")
                .addToLore("§8Click to choose start price")
                .addToLore("§8Higher start price costs more");
        CoreItem instant = CoreItem.generate(Material.GOLD_BLOCK)
                .setName("§aChoose Instant Buy Price")
                .addToLore("§8Current Price: §e" + (instantBuy == 0 ? "Not Enabled" : instantBuy + " Coins"))
                .addToLore("§8Current Costs: §e" + (instantBuy / 300) + " Coins")
                .addToLore("§8Click to choose instant buy price")
                .addToLore("§8Higher instant buy price costs more");
        AtomicReference<CoreItem> createAuction = new AtomicReference<>(CoreItem.generate(Material.DIAMOND)
                .setName(item.isSimilar(noItem) ? "§cNo item selected" : "§aCreate auction")
                .addToLore("§8Publish auction"));
        InventoryBuilder builder = InventoryBuilder.create("New Auction", 6)
                .openBlocked(player)
                .fillAll()
                .addItem(item, 2, 5)
                .addItem(createAuction.get(), 4, 2)
                .addItem(coinsNext, 4, 5)
                .addItem(instant, 4, 6)
                .addItem(timeNext, 4, 8)
                .addItem(back, 6, 5);
        player.inventories().registerItemClick(timeNext, this::openAuctionTimeChoose);
        player.inventories().onItemInOwnInvClick(clicked -> {
            this.item = clicked;
            builder.addItem(item, 2, 5);
            player.inventories().unregisterItemListeners(createAuction.get());
            createAuction.set(CoreItem.generate(Material.DIAMOND)
                    .setName(item.isSimilar(noItem) ? "§cNo item selected" : "§aCreate auction")
                    .addToLore("§8Publish auction"));
            builder.addItem(createAuction.get(), 4, 2);
            player.inventories().registerItemClick(createAuction.get(), this::createAuction);
        });
        player.inventories().registerItemClick(coinsNext, () -> ArematicsExecutor.runAsync(() -> {
            try {
                String result = ArematicsExecutor.awaitAnvilResult("startPrice", String.valueOf(this.bitStart), player);
                this.bitStart = Double.parseDouble(result);
            } catch (Exception ignore) {}
            this.openBaseInventory();
        }));
        player.inventories().registerItemClick(instant, () -> ArematicsExecutor.runAsync(() -> {
            try {
                String result = ArematicsExecutor.awaitAnvilResult("buyPrice", String.valueOf(this.instantBuy), player);
                this.instantBuy = Double.parseDouble(result);
            } catch (Exception ignore) {}
            this.openBaseInventory();
        }));
    }

    public void openAuctionTimeChoose(){
        CoreItem back = CoreItem.create(Items.BACK.clone())
                .removeMeta(CoreItem.BINDED_COMMAND);
        CoreItem timeOneHour = timeItem(Period.hours(1));
        CoreItem timeSixHour = timeItem(Period.hours(6));
        CoreItem timeTwelveHour = timeItem(Period.hours(12));
        CoreItem timeOneDay = timeItem(Period.days(1));
        CoreItem timeThreeDays = timeItem(Period.days(3));
        InventoryBuilder.create("Auction Time", 3)
                .openBlocked(player)
                .fillAll()
                .addItem(timeOneHour, 2, 2)
                .addItem(timeSixHour, 2, 3)
                .addItem(timeTwelveHour, 2, 4)
                .addItem(timeOneDay, 2, 5)
                .addItem(timeThreeDays, 2, 6)
                .addItem(back, 3, 5);
        player.inventories().registerItemClick(back, this::openBaseInventory);
        player.inventories().registerItemClick(timeOneHour, () -> onItemClick(Period.hours(1)));
        player.inventories().registerItemClick(timeSixHour, () -> onItemClick(Period.hours(6)));
        player.inventories().registerItemClick(timeTwelveHour, () -> onItemClick(Period.hours(12)));
        player.inventories().registerItemClick(timeOneDay, () -> onItemClick(Period.days(1)));
        player.inventories().registerItemClick(timeThreeDays, () -> onItemClick(Period.days(3)));
    }

    private void onItemClick(Period period){
        this.time = period;
        this.openBaseInventory();
    }

    private void createAuction(){
        if(item.isSimilar(noItem)){
            player.warn("Please select a item for your auction").handle();
        }else{
            AuctionService auctionService = Boots.getBoot(CoreBoot.class).getContext().getBean(AuctionService.class);
            AuctionCategoryService auctionCategoryService = Boots.getBoot(CoreBoot.class).getContext().getBean(AuctionCategoryService.class);
            AuctionCategory category = new AuctionCategory("köks", new CoreItem[]{item}, new CoreItem[]{item});
            category = auctionCategoryService.save(category);
            AuctionType type = bitStart == 0 ? AuctionType.INSTANT_BUY : instantBuy == 0 ? AuctionType.BID : AuctionType.ALL;
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(time.toStandardHours().getHours()));
            Auction auction = new Auction(null, player.getUUID(), bitStart, instantBuy, new CoreItem[]{item}, category, type, timestamp, new HashSet<>());
            auctionService.save(auction);
            player.getPlayer().closeInventory();
            player.getPlayer().getInventory().remove(item);
            player.info("Auction created").handle();
        }
    }

    private CoreItem timeItem(Period period){
        CoreItem item = CoreItem.generate(Material.WATCH)
                .setName("§aTime: " + TimeUtils.toString(period))
                .addToLore("§8Set Auction time to §e" + TimeUtils.toString(period))
                .addToLore("§8Costs: §e" + (period.toStandardHours().getHours() * 100) + " Coins");
        if(time.toStandardHours().equals(period.toStandardHours())) return item.setGlow();
        return item;
    }
}
