package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UiCommand extends CoreCommand {

    private final UserService service;
    private final InventoryService inventoryService;

    @Autowired
    public UiCommand(UserService userService, InventoryService inventoryService){
        super("ui");
        this.service = userService;
        this.inventoryService = inventoryService;
    }

    private Part asPart(CommandSender sender, String name){
        return PartBuilder.createHoverAndSuggest(name,
                LanguageAPI.prepareRawMessage(sender, "preferred_mode_set").replaceAll("%mode%", name),
                "/ui " + name);
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        MSG modes = MSGBuilder.join(',', asPart(sender, "cli"), asPart(sender, "gui"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", modes)
                .handle();
        return true;
    }

    @Override
    protected boolean onDefaultGUI(CorePlayer player) {
        Inventory inv = inventoryService.getOrCreate("preferred.mode.default", "§9User Interface", (byte) 9);
        player.openInventory(inv);
        return true;
    }

    @SubCommand("{mode}")
    public boolean setPreferredMode(CorePlayer player, String mode) {
        if(Arrays.stream(new String[]{"cli", "gui"}).noneMatch(m -> m.equalsIgnoreCase(mode)))
            onDefaultExecute(player.getPlayer());
        User user = service.getUserByUUID(player.getUUID());
        user.getConfigurations().put("command-mode", new Configuration(mode));
        service.update(user);
        player.info("UI mode switch to " + mode).handle();
        return true;
    }
}
