package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.inventories.helper.Range;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.*;
import com.arematics.minecraft.data.service.AuctionCategoryService;
import com.arematics.minecraft.data.service.AuctionService;
import com.arematics.minecraft.data.service.PlayerAuctionSettingsService;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AuctionCommand extends CoreCommand {

    private final Server server;
    private final AuctionService auctionService;
    private final AuctionCategoryService auctionCategoryService;
    private final PlayerAuctionSettingsService playerAuctionSettingsService;

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
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)){
            sender.sendMessage("This command is only for players");
            return;
        }
        CorePlayer player = CorePlayer.get((Player) sender);
        Inventory inv = Bukkit.createInventory(null, 3*9, "§8Auctions");
        player.openTotalBlockedInventory(inv);
        InventoryPlaceholder.fillInventory(inv, DyeColor.BLACK);
        inv.setItem(8 + 3, CoreItem.generate(Material.DIAMOND_BLOCK)
                .bindCommand("auction search")
                .setName("§bView All Auctions"));
        inv.setItem(8 + 5, CoreItem.generate(Material.BOOK)
                .bindCommand("auction bids")
                .setName("§bYour Bids"));
        inv.setItem(8 + 7, CoreItem.generate(Material.CHEST)
                .bindCommand("auction sells")
                .setName("§bManage your auctions"));
    }

    @SubCommand("search")
    public void searchMarket(CorePlayer player) {
        PlayerAuctionSettings settings = playerAuctionSettingsService.findOrCreateDefault(player.getUUID());
        Inventory inv = Bukkit.createInventory(null, 6*9, "§8Auction Search");
        player.openTotalBlockedInventory(inv);
        InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
        inv.setItem(5 * 9 + 4, CoreItem.generate(Material.COMPASS)
                .bindCommand("auction")
                .setName("§bBack to menu"));
        CoreItem type = CoreItem.generate(Material.PAPER)
                .bindCommand("auction next type")
                .setName("§bAuction Type")
                .bindEnumLore(settings.getAuctionType())
                .register(server, player, eventItem -> eventItem
                        .bindEnumLore(updateContent(player).getAuctionType()));
        inv.setItem(5 * 9 + 5, type);
        CoreItem sort = CoreItem.generate(Material.DROPPER)
                .bindCommand("auction next sort")
                .setName("§bAuction Sort")
                .bindEnumLore(settings.getAuctionSort())
                .register(server, player, eventItem -> eventItem
                        .bindEnumLore(updateContent(player).getAuctionSort()));
        inv.setItem(5 * 9 + 6, sort);
        updateItems(inv, settings);
    }

    private PlayerAuctionSettings updateContent(CorePlayer player){
        InventoryView view = player.getView();
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
                .addToLore("    §8> Could not find items for this filter");
        Page<Auction> auctions = auctionService.findAllByFilter(settings, 0);
        Range range = Range.allHardInRows(1, 8, 1, 2, 3, 4);
        InventoryPlaceholder.clear(inv, range);
        if(auctions.getContent().isEmpty()) InventoryPlaceholder.fillFree(inv, item);
        auctions.forEach(auction -> inv.addItem(auction.getSell()[0].setName("§7ID: §c" + auction.getAuctionId())));
    }
    
    @SubCommand("bids")
    public void showCurrentBids(CorePlayer player) {
        throw new CommandProcessException("Command not working at the moment");
    }

    @SubCommand("sells")
    public void shopOwnSells(CorePlayer player) {

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
}
