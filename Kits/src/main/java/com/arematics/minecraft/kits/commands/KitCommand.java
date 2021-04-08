package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.entities.player.inventories.paging.Paging;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.KitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class KitCommand extends CoreCommand {
    private final KitService service;
    private final InventoryService inventoryService;

    @Autowired
    public KitCommand(KitService kitService, InventoryService inventoryService) {
        super("kit", "kits");
        this.service = kitService;
        this.inventoryService = inventoryService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Supplier<Page<Kit>> paging = () -> service.findKitNames(sender.inventories().getPage());
        Paging.createWithMapper(sender, paging)
                .onCLI("Kit", "kit")
                .onGUI(this::createInventory)
                .execute();
    }

    private void createInventory(CorePlayer sender, Supplier<Page<Kit>> paging){
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Kit> binder = PageBinder.of(paging, range, server);
        InventoryBuilder.create("Kits", 6)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true);
    }

    @SubCommand("{kit}")
    public void giveKit(CorePlayer player, Kit kit) {
        giveToPlayer(kit, player, player.hasPermission("kit.force"));
    }

    private void giveToPlayer(Kit kit, CorePlayer player, boolean force) throws CommandProcessException {
        if(!force && !service.isPermitted(player.getPlayer(), kit)) throw new CommandProcessException("cmd_noperms");
        if(!force && service.hasCooldownOnKit(player.getUUID(), kit)){
            service.getCooldownOnKit(player.getUUID(), kit)
                    .ifPresent(cooldown -> player.warn("You need to wait until " +
                            TimeUtils.toString(cooldown.getEndTime()) +
                            " to collect this kit again").handle());
            return;
        }
        WrappedInventory inv = inventoryService.findOrCreate("kit.inventory." + kit.getName(), "§6Kit " + kit.getName(),
                (byte)27);
        service.setCooldownOnKit(player.getUUID(), kit);
        server.items().giveItemsTo(player, inv.getOpen().getContents());
    }
}
