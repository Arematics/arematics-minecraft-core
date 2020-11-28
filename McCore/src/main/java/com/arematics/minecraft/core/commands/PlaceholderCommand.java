package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaceholderCommand extends CoreCommand {

    private final PlaceholderController placeholderController;

    @Autowired
    public PlaceholderCommand(PlaceholderController placeholderController) {
        super("placeholder");
        this.placeholderController = placeholderController;
    }

    @SubCommand("list")
    public boolean list(CorePlayer player) {
        List<Part> parts = new ArrayList<>();
        placeholderController.getPlaceholders().values().forEach(globalPlaceholder -> {
            parts.add(new Part(globalPlaceholder.getPlaceholderKey()));
        });
        player.info("listing").setInjector(AdvancedMessageInjector.class).
                replace("list_type", new Part("Placeholder")).replace("list_value", MSGBuilder.join(parts, ',')).handle();
        return true;
    }

    @SubCommand("inspect")
    public boolean inspect(CorePlayer player) {
        execInspect(player, player);
        return true;
    }

    @SubCommand("inspect {targetName}")
    public boolean inspect(CorePlayer player, CorePlayer target) {
        execInspect(player, target);
        return true;
    }

    private void execInspect(CorePlayer player, CorePlayer target) {
        List<Part> parts = new ArrayList<>();
        placeholderController.getPlaceholders().values().forEach(globalPlaceholder -> {
            parts.add(new Part(globalPlaceholder.getPlaceholderKey() + " = " + globalPlaceholder.getValue(target.getPlayer())));
        });
        player.info("placeholder_inspect").setInjector(AdvancedMessageInjector.class).replace("player", new Part(target.getPlayer().getDisplayName())).
                replace("placeholders", MSGBuilder.join(parts, ' ')).handle();


    }


}
