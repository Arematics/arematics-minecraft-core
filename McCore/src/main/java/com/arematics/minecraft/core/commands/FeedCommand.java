package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "feed", description = "Feed yourself")
public class FeedCommand extends CoreCommand {

    public FeedCommand() {
        super("feed", "eat", "essen");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        CorePlayer player = CorePlayer.get((Player) sender);
        player.getPlayer().setFoodLevel(25);
        player.info("Your hunger was satisfied").handle();
    }
}
