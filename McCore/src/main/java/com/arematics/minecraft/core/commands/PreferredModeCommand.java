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
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PreferredModeCommand extends CoreCommand {

    private final UserService service;
    private final InventoryService inventoryService;

    @Autowired
    public PreferredModeCommand(UserService userService, InventoryService inventoryService){
        super("preferred-mode");
        this.service = userService;
        this.inventoryService = inventoryService;
    }

    private Part asPart(CommandSender sender, String name){
        return PartBuilder.createHoverAndSuggest(name,
                LanguageAPI.prepareRawMessage(sender, "preferred_mode_set").replaceAll("%mode%", name),
                "/preferred-mode " + name);
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        MSG modes = MSGBuilder.join(',', asPart(sender, "cli"), asPart(sender, "ui"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", modes)
                .handle();
        return true;
    }

    @Override
    protected boolean onDefaultUI(CorePlayer player) {
        Inventory inv = inventoryService.getOrCreate("preferred.mode.default", "§9Preferred Mode", (byte) 9);
        player.openInventory(inv);
        return true;
    }

    @SubCommand("{mode}")
    public boolean setPreferredMode(Player player, String mode) {
        if(Arrays.stream(new String[]{"cli", "ui"}).noneMatch(m -> m.equalsIgnoreCase(mode))) onDefaultExecute(player);
        User user = service.getUserByUUID(player.getUniqueId());
        user.getConfigurations().put("command-mode", new Configuration(mode));
        service.update(user);
        return true;
    }
}
