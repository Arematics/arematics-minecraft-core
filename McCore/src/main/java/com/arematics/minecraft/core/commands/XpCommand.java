package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.player.xp", description = "send xp to a player")
public class XpCommand extends CoreCommand {
    public XpCommand() {
        super("xp", "xppay","payxp");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.info("Command coming soon").handle();
    }

    /*@SubCommand("{target} {amount}")
    public void sendXp(CorePlayer player, CorePlayer target, Integer amount) {
        sendExperience(player, target, amount);
    }

    private static void sendExperience(CorePlayer player, CorePlayer target, int amount) {


    }*/


}
