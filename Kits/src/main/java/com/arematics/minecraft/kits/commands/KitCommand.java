package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.entities.player.inventories.paging.Paging;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.KitService;
import com.arematics.minecraft.data.service.UserService;
import com.arematics.minecraft.data.share.model.Cooldown;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Supplier;

@Component
public class KitCommand extends CoreCommand {
    public static final String PAGER_KEY = "kits";

    private final KitService service;
    private final UserService userService;
    private final InventoryService inventoryService;

    @Autowired
    public KitCommand(KitService kitService, UserService userService, InventoryService inventoryService) {
        super("kit", "kits");
        this.service = kitService;
        this.userService = userService;
        this.inventoryService = inventoryService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Supplier<Page<Kit>> paging =
                () -> service.findKitNames(sender.inventories().getPage());
        Paging.createWithMapper(sender, paging)
                .onCLI("Ignored", "ignore list")
                .onGUI(this::createInventory)
                .execute();
    }

    private void createInventory(CorePlayer sender, Supplier<Page<Kit>> paging){
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Kit> binder = PageBinder.of(paging, range);
        InventoryBuilder.create("Kits", 6)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true);
    }

    @SubCommand("{kit}")
    public void giveKit(CorePlayer player, Kit kit) {
        try{
            giveToPlayer(kit, player, player.hasPermission("kit.force"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void giveToPlayer(Kit kit, CorePlayer player, boolean force){
        if(!force && !service.isPermitted(player.getPlayer(), kit)){
            player.warn("cmd_noperms").handle();
            return;
        }
        if(!force && service.hasCooldownOnKit(player.getUUID(), kit)){
            Cooldown cooldown = service.getCooldownOnKit(player.getUUID(), kit).orElse(null);
            player.warn("You need to wait until " + TimeUtils.toString(cooldown.getEndTime()) + " to collect this kit again").handle();
            return;
        }
        Inventory inv = inventoryService.getOrCreate("kit.inventory." + kit.getName(), "§6Kit " + kit.getName(),
                (byte)27);
        ItemStack[] items = Arrays.stream(inv.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .toArray(ItemStack[]::new);
        service.setCooldownOnKit(player.getUUID(), kit);
        Items.giveItem(player, items);
    }
}
