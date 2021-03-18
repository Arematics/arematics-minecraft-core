package com.arematics.minecraft.homes.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.supplier.standard.CommandSupplier;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.Home;
import com.arematics.minecraft.data.mode.model.HomeId;
import com.arematics.minecraft.data.service.HomeService;
import org.bukkit.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Supplier;

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
    public void onDefaultExecute(CorePlayer sender) {
        queryHomes(sender,  false);
    }

    @SubCommand("{name}")
    public void teleportToHome(CorePlayer sender, String name) {
        HomeId id = new HomeId(sender.getUUID(), name);
        try{
            Home home = service.findByOwnerAndName(id);
            sender.teleport(home.getLocation()).schedule();
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
        home.setLocation(sender.getLocation());
        service.save(home);
        sender.info("Home location for " + (newHome ? "new home" : "your home") + " " + name + " has been set").handle();
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

    @SubCommand("query {deleteMode}")
    public void queryHomes(CorePlayer sender, Boolean deleteMode) {
        queryHomes(sender, deleteMode, null);
    }

    @SubCommand("query {deleteMode} {contains}")
    public void queryHomes(CorePlayer sender, Boolean deleteMode, String query) {
        Supplier<Page<Home>> homes = () -> query == null ?
                service.findAllByOwner(sender.getUUID(), sender.inventories().getPage()) :
                service.findAllByOwnerAndSearch(sender.getUUID(), query, sender.inventories().getPage());
        CommandSupplier.create()
                .setCLI((player -> queryCli(player, homes, query)))
                .setGUI((player) -> openInventoryQuery(player, homes, deleteMode, query))
                .accept(sender);
    }

    private void queryCli(CorePlayer sender, Supplier<Page<Home>> paging, String query){
        CommandUtils.sendPagingList(sender, paging,  Home::mapToMessage,"Home",
                "buffs fetch " + "home query false " + (query == null ? "" : " " + query));
    }

    private MSG homePart(Home home){
        Part teleport = PartBuilder.createHoverAndRun("§a" + home.getName(),
                "§aTeleport to home " + home.getName(),
                "/home " + home.getName());
        Part delete = PartBuilder.createHoverAndSuggest(" §8[§cX§8]",
                "§cDelete home " + home.getName(),
                "/delhome "+ home.getName());
        return new MSG(teleport, delete);
    }

    private void openInventoryQuery(CorePlayer sender, Supplier<Page<Home>> paging, Boolean deleteMode, String query){
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Home> homes = PageBinder.of(paging, range, home -> fetchHomeItem(home, query, deleteMode));
        CoreItem searchBook = CoreItem.generate(Material.BOOK_AND_QUILL)
                .bindCommand("home query " + deleteMode + " {contains}")
                .setName("§aSearch homes by query")
                .addToLore("§7Current Query: §b" + (query != null ? query : "None"));
        CoreItem searchClear = CoreItem.generate(Material.BOOK)
                .bindCommand("home query " + deleteMode)
                .setName("§eRemove search query");
        CoreItem modeItem = deleteModeItem(deleteMode, query);
        InventoryBuilder.create(deleteMode ? "§cDelete Homes" : "Homes", 6)
                .openBlocked(!deleteMode ? "§cDelete Homes" : "Homes", sender)
                .fillOuterLine()
                .bindPaging(sender, homes, deleteMode)
                .addItem(searchBook, 1, 3)
                .addItem(searchClear, 1, 4)
                .addItem(modeItem, 6, 5);
        if(!deleteMode) sender.inventories().onEmptySlotClick(clicked -> sender.dispatchCommand("home set {name}"));
    }

    private CoreItem deleteModeItem(Boolean deleteMode, String query){
        return CoreItem.generate(deleteMode ? Material.ENDER_PORTAL_FRAME : Material.REDSTONE_BLOCK)
                .bindCommand("home query " + !deleteMode + (query != null ? " " + query : ""))
                .setName("§cEnable " + (deleteMode ? "Teleport" : "Delete") + " Mode");
    }

    private CoreItem fetchHomeItem(Home home, String query, Boolean deleteMode){
        if(!deleteMode) return CoreItem.generate(Material.BED)
                .bindCommand("home " + home.getName())
                .setName("§cHome: §7" + split(home.getName(), query));
        else return CoreItem.generate(Material.BED)
                .bindCommand("delhome " + home.getName())
                .setName("§cRemove §7" + split(home.getName(), query));
    }

    private String split(String name, String query){
        if(query == null) return name;
        return name.replaceAll(query, "§b" + query + "§7");
    }
}
