package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.core.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@PluginCommand(aliases = {"globalplaceholder"})
@Component
public class PlaceholderCommand extends CoreCommand {

    private final PlaceholderController placeholderController = ChatAPI.getPlaceholderController();


    public PlaceholderCommand() {
        super("placeholder");
    }

    @Override
    @Default
    public boolean onDefaultExecute(CommandSender sender) {
        sender.sendMessage("lulw");
        return false;
    }

    @SubCommand("list")
    public boolean list(Player player) {
        placeholderController.getPlaceholders().values().forEach(globalPlaceholder -> player.sendMessage(globalPlaceholder.getPlaceholderKey()));
        return true;
    }

    @SubCommand("inspect")
    public boolean inspect(Player player) {
        execInspect(player, player);
        return true;
    }

    @SubCommand("inspect {targetName}")
    public boolean inspect(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        player.sendMessage("Zeige Placeholder values für " + target.getDisplayName());
        execInspect(player, target);
        return true;
    }

    private void execInspect(Player player, Player target) {
        placeholderController.getPlaceholders().values().forEach(globalPlaceholder -> {
            player.sendMessage(globalPlaceholder.getPlaceholderKey() + " | " + globalPlaceholder.getValue(target));
        });
    }



}
