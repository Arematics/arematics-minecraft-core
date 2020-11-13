package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CommandSupplier;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.pages.Pageable;
import com.arematics.minecraft.core.pages.Pager;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.IgnoredService;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IgnoreCommand extends CoreCommand {
    private static final String PAGER_KEY = "ignore list";

    private final IgnoredService service;
    private final UserService userService;

    @Autowired
    public IgnoreCommand(IgnoredService ignoredService, UserService userService){
        super("ignore");
        this.service = ignoredService;
        this.userService = userService;
    }

    @SubCommand("add {target}")
    public boolean addIgnoredUser(CorePlayer player, User target) {
        if(target.getRank().isInTeam()){
            Messages.create("You can't ignore team members").WARNING().to(player.getPlayer()).handle();
            return true;
        }
        if(!service.hasIgnored(player.getUUID(), target.getUuid()))
            service.ignore(player.getUUID(), target.getUuid());
        Messages.create("Successful ignored player: " + target.getLastName()).to(player.getPlayer()).handle();
        Pageable pageable = player.getPager().fetch(IgnoreCommand.PAGER_KEY);
        if(pageable != null){
            pageable.add(target.getLastName());
        }
        return true;
    }

    @SubCommand("rem {target}")
    public boolean remIgnoredUser(CorePlayer player, User target) {
        if(service.hasIgnored(player.getUUID(), target.getUuid()))
            service.unIgnore(player.getUUID(), target.getUuid());
        Messages.create("Successful unignored player: " + target.getLastName()).to(player.getPlayer()).handle();
        Pageable pageable = player.getPager().fetch(IgnoreCommand.PAGER_KEY);
        if(pageable != null)
            pageable.remove(target.getLastName());
        return true;
    }

    @SubCommand("list")
    public boolean listIgnored(CorePlayer player) {
        Pageable pageable = player.getPager().fetch(IgnoreCommand.PAGER_KEY);
        if(pageable == null) {
            List<String> ignoredNames = service.fetchAllIgnored(player.getUUID()).stream()
                    .map(uuid -> this.userService.getUserByUUID(uuid).getLastName())
                    .collect(Collectors.toList());
            pageable = player.getPager().create(IgnoreCommand.PAGER_KEY, ignoredNames);
        }
        Page current = pageable.current();
        return CommandSupplier.create().setCLI(sender -> onCLI(player, current)).setUI(sender -> onUI(player, current))
                .accept(player.getPlayer());
    }

    private boolean onCLI(CorePlayer player, Page page){
        MSG result = page == null ? new MSG("-") : MSGBuilder.join(page.getContent().stream()
                        .map(this::toPart)
                        .collect(Collectors.toList()), ',');
        result.PARTS.forEach(part -> part.setBaseColor(JsonColor.RED));
        Messages.create("ignored_player_list")
                .to(player.getPlayer())
                .setInjector(AdvancedMessageInjector.class)
                .replace("playerNames", result)
                .handle();
        if(page != null) Pager.sendDefaultPageMessage(player, IgnoreCommand.PAGER_KEY);
        return true;
    }

    private boolean onUI(CorePlayer player, Page page){
        if(page == null) page = new Page(new ArrayList<>());
        Inventory inventory = page.getInventory();
        if(inventory == null) {
            inventory = Bukkit.createInventory(null, 54, "§cIgnored Players");
            for (int i = 0; i <= 8; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            for (int i = 45; i <= 53; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            if (player.getPager().fetch(IgnoreCommand.PAGER_KEY).hasBefore()) inventory.setItem(45, Items.BEFORE_PAGE);
            if (player.getPager().fetch(IgnoreCommand.PAGER_KEY).hasNext()) inventory.setItem(53, Items.NEXT_PAGE);
            int slot = 9;
            for (String ignored : page.getContent())
                inventory.setItem(slot++, Items.fetchPlayerSkull(ignored)
                        .bindCommand("ignore rem " + ignored)
                        .setName("§8Player: §c" + ignored)
                        .addToLore("§cClick to unignore player"));
            page.setInventory(inventory);
        }
        Inventory finalInventory = inventory;
        ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(finalInventory));
        return true;
    }

    //TODO Remove plain text
    private Part toPart(String name){
        return new Part(name)
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Unignore §c" + name)
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/ignore rem " + name);
    }
}
