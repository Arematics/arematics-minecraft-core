package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "team.punishment.kick", description = "Kick a player from the network")
public class KickCommand extends CoreCommand {

    @Autowired
    public KickCommand(){
        super("kick", true);
    }

    @SubCommand("{player}")
    public void kickPlayer(CorePlayer sender, CorePlayer target) {
        if(target.getUser().getRank().isInTeam())
            throw new CommandProcessException("Can not kick team members");
        MessagingUtils.kickPlayer(target, "§cYou got kicked");
        sender.info("Player " + target.getName() + " got kicked").handle();
    }
}
