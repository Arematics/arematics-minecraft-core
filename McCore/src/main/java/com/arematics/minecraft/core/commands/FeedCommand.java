package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.player.feed", description = "Feed yourself")
public class FeedCommand extends CoreCommand {

    public FeedCommand() {
        super("feed", "eat", "essen");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.getPlayer().setFoodLevel(25);
        sender.info("player_hunger_satisfied").handle();
    }
}
