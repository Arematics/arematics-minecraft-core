package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.entities.player.inventories.paging.Paging;
import com.arematics.minecraft.data.global.model.Ignored;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.IgnoredService;
import com.arematics.minecraft.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

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
    public void addIgnoredUser(CorePlayer player, User target) {
        if(target.getRank().isInTeam()) throw new CommandProcessException("player_ignore_team_members");
        if(!service.hasIgnored(player.getUUID(), target.getUuid())) service.ignore(player.getUUID(), target.getUuid());
        Messages.create("Successful ignored player: " + target.getLastName()).to(player.getPlayer()).handle();
    }

    @SubCommand("rem {target}")
    public void remIgnoredUser(CorePlayer player, User target) {
        if(service.hasIgnored(player.getUUID(), target.getUuid())) service.unIgnore(player.getUUID(), target.getUuid());
        player.info("Successful unignored player: " + target.getLastName()).handle();
    }

    @SubCommand("list")
    public void listIgnored(CorePlayer sender) {
        Supplier<Page<Ignored>> paging =
                () -> service.fetchAllIgnored(sender.getUUID(), sender.inventories().getPage());
        Paging.createWithMapper(sender, paging)
                .onCLI("Ignored", "ignore list")
                .onGUI(this::createInventory)
                .execute();
    }

    private void createInventory(CorePlayer sender, Supplier<Page<Ignored>> paging){
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Ignored> binder = PageBinder.of(paging, range);
        InventoryBuilder.create("Ignored", 6)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true);
    }
}
