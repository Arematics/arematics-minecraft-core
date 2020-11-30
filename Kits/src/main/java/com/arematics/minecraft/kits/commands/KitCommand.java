package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.supplier.page.PageCommandSupplier;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.pages.Pageable;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.KitService;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)){
            Messages.create("Only Players allowed to perform this command")
                    .WARNING()
                    .to(sender)
                    .handle();
            return;
        }
        CorePlayer player = CorePlayer.get((Player)sender);
        Pageable pageable = player.getPager().fetchOrCreate(KitCommand.PAGER_KEY, (p) -> service.findKitNames());
        PageCommandSupplier.create(pageable.current()).setCLI(this::onCLI).setGUI(this::onUI).accept(player);
    }

    private boolean onCLI(CorePlayer player, Page page){
        String msg = "§a\n\n§7Kits" + " » " + "%kits%";
        MSG result = page == null ? new MSG("-") : MSGBuilder.join(page.getContent().stream()
                .map(kit -> PartBuilder.createHoverAndSuggest(kit, "Get kit " + kit, "/kit " + kit))
                .collect(Collectors.toList()), ',');
        result.PARTS.forEach(part -> part.setBaseColor(JsonColor.RED));
        Messages.create(msg)
                .to(player.getPlayer())
                .setInjector(AdvancedMessageInjector.class)
                .replace("kits", result)
                .disableServerPrefix()
                .handle();
        return true;
    }

    private boolean onUI(CorePlayer player, Page page){
        if(page == null) page = new Page(new ArrayList<>());
        Inventory inventory = page.getInventory();
        if(inventory == null) {
            inventory = Bukkit.createInventory(null, 54, "§cKits");
            for (int i = 0; i <= 8; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            for (int i = 45; i <= 53; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            if (player.getPager().fetch(KitCommand.PAGER_KEY).hasBefore()) inventory.setItem(45, Items.BEFORE_PAGE);
            if (player.getPager().fetch(KitCommand.PAGER_KEY).hasNext()) inventory.setItem(53, Items.NEXT_PAGE);
            int slot = 9;
            for (String kitName : page.getContent())
                inventory.setItem(slot++, this.service.findKit(kitName)
                        .getDisplayItem()[0]
                        .bindCommand("kit " + kitName)
                        .closeInventoryOnClick()
                        .setName("§8Kit: §c" + kitName)
                        .addToLore("§cClick to get kit"));
            page.setInventory(inventory);
        }
        Inventory finalInventory = inventory;
        player.openInventory(finalInventory);
        return true;
    }

    @SubCommand("{kit}")
    public boolean giveKit(CorePlayer player, Kit kit) {
        giveToPlayer(kit, player.getPlayer(), Permissions.hasPermission(player.getUser(), "kit.force"));
        return true;
    }

    private void giveToPlayer(Kit kit, Player player, boolean force){
        if(!force && !service.isPermitted(player, kit)){
            Messages.create("cmd_noperms")
                    .WARNING()
                    .to(player)
                    .handle();
            return;
        }
        Inventory inv = inventoryService.getOrCreate("kit.inventory." + kit.getName(), "§6Kit " + kit.getName(),
                (byte)27);
        ItemStack[] items = Arrays.stream(inv.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .toArray(ItemStack[]::new);
        player.getInventory().addItem(items);
    }
}
