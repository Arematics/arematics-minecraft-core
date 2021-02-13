package com.arematics.minecraft.homes.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.Home;
import com.arematics.minecraft.data.mode.model.HomeId;
import com.arematics.minecraft.data.service.HomeService;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HomeCommand extends CoreCommand {

    private final Server server;
    private final HomeService service;

    @Autowired
    public HomeCommand(Server server,
                       HomeService homeService){
        super("home", "homes");
        this.server = server;
        this.service = homeService;
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        if(!(sender instanceof Player)) return true;
        CorePlayer player = CorePlayer.get((Player) sender);
        queryHomes(player, null, 0);
        return true;
    }

    @Override
    protected boolean onDefaultGUI(CorePlayer player) {
        listInventoryPages(player, 0, false);
        return true;
    }

    @SubCommand("teleport {name}")
    public void teleportHome(CorePlayer sender, String name) {
        HomeId id = new HomeId(sender.getUUID(), name);
        try{
            Home home = service.findByOwnerAndName(id);
            sender.teleport(home.getLocation());
        }catch (RuntimeException re){
            throw new CommandProcessException("Home with name: " + name + " not exists");
        }
    }

    @SubCommand("set {name}")
    public void setHome(CorePlayer sender, String name) {
        HomeId id = new HomeId(sender.getUUID(), name);
        boolean newHome = true;
        Home home;
        try{
            home = service.findByOwnerAndName(id);
            newHome = false;
        }catch (RuntimeException re){
            home = new Home(sender.getUUID(), name, sender.getLocation(), Timestamp.valueOf(LocalDateTime.now()));
        }
        service.save(home);
        sender.info("Home location for " + (newHome ? "new home" : "your home") + " " + name + " has been changed").handle();
    }

    @SubCommand("delete {name}")
    public void deleteHome(CorePlayer sender, String name) {
        HomeId id = new HomeId(sender.getUUID(), name);
        try{
            Home home = service.findByOwnerAndName(id);
            service.delete(home);
            sender.info("Home with name: " + name + " has been removed");
        }catch (RuntimeException re){
            throw new CommandProcessException("Home with name: " + name + " not exists");
        }
    }

    @SubCommand("inventory {page} {deleteMode}")
    public void listInventoryPages(CorePlayer sender, Integer page, Boolean deleteMode) {
        listInventoryPagesQuery(sender, page, deleteMode, null);
    }

    @SubCommand("inventory {page} {deleteMode} {startsWith}")
    public void listInventoryPagesQuery(CorePlayer sender, Integer page, Boolean deleteMode, String query) {
        Page<Home> homes = query == null ?
                service.findAllByOwner(sender.getUUID(), page) :
                service.findAllByOwnerAndSearch(sender.getUUID(), query, page);
        openInventoryQuery(sender, homes, deleteMode, query);
    }

    @SubCommand("query {query} {page}")
    public void queryHomes(CorePlayer sender, String query, Integer page) {
        Page<Home> homes = query == null ?
                service.findAllByOwner(sender.getUUID(), page) :
                service.findAllByOwnerAndSearch(sender.getUUID(), query, page);
        List<Part> parts = homes.getContent().stream().map(this::homePart).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Home"))
                .replace("list_value", MSGBuilder.join(parts, ','))
                .handle();
        if(homes.hasNext() || homes.hasPrevious()){
            CommandUtils.sendPreviousAndNext(sender,
                    "home query " + query + " " + (homes.hasPrevious() ? page - 1 : page),
                    "home query " + query + " " + (homes.hasNext() ? page + 1 : page));
        }
    }

    private Part homePart(Home home){
        return PartBuilder.createHoverAndSuggest(home.getName(),
                "§cDelete home " + home.getName(),
                "/delhome " + home.getName());
    }

    private void openInventoryQuery(CorePlayer sender, Page<Home> homes, Boolean deleteMode, String query){
        Inventory inv = Bukkit.createInventory(null, 54, deleteMode ? "§cDelete Homes" : "§6Homes");
        sender.openTotalBlockedInventory(inv);
        InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
        if(!deleteMode)
            inv.setItem(1 + 5, CoreItem.generate(Material.REDSTONE_BLOCK)
                    .bindCommand("home inventory " + homes.getNumber() + " true" + (query != null ? " " + query : ""))
                    .setName("§cEnable Delete Mode"));
        else
            inv.setItem(1 + 5, CoreItem.generate(Material.ENDER_PORTAL_FRAME)
                    .bindCommand("home inventory " + homes.getNumber() + " false" + (query != null ? " " + query : ""))
                    .setName("§cEnable Teleport Mode"));
        sender.setEmptySlotClick(clicked -> sender.dispatchCommand("home set {name}"));
        if(!deleteMode)
            homes.getContent().forEach(home -> inv.addItem(CoreItem.generate(Material.BED)
                    .bindCommand("home teleport " + home.getName())
                    .setName("§cHome: §7" + home.getName())));
        else
            homes.getContent().forEach(home -> inv.addItem(CoreItem.generate(Material.BED)
                    .bindCommand("delhome " + home.getName())
                    .setName("§cRemove §7" + home.getName())
                    .register(server, sender, (item) -> null)));
        inv.setItem(1 + 1, CoreItem.generate(Material.BOOK_AND_QUILL)
                .bindCommand("home inventory 0 " + deleteMode + " {startsWith}")
                .setName("§bSearch homes by startsWith"));
        inv.setItem(1 + 2, CoreItem.generate(Material.BOOK)
                .bindCommand("home inventory 0 " + deleteMode)
                .setName("§bRemove search query"));
        if(homes.hasNext())
            inv.setItem(6*9 - 1, CoreItem.generate(Material.ARROW)
                    .bindCommand("home inventory "
                            + (homes.getNumber() + 1) + " "
                            + (deleteMode ? "true":"false")
                            + (query != null ? " " + query : ""))
                    .setName("§cNext Page"));
        if(homes.hasPrevious())
            inv.setItem(5*9, CoreItem.generate(Material.ARROW)
                    .bindCommand("home inventory "
                            + (homes.getNumber() - 1) + " "
                            + (deleteMode ? "true":"false")
                            + (query != null ? " " + query : ""))
                    .setName("§cPage Before"));
    }
}
