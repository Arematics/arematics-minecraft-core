package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.data.global.model.Ban;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.BanService;
import org.bukkit.command.CommandSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@Perm(permission = "punishment-unban", description = "Unban a player")
public class UnbanCommand extends CoreCommand {

    private final BanService service;

    @Autowired
    public UnbanCommand(BanService banService){
        super("unban");
        this.service = banService;
    }

    @SubCommand("{target}")
    public void unbanPlayer(CommandSender sender, User target) {
        try{
            Ban ban = service.getBan(target.getUuid());
            if(ban.getBannedUntil() != null && ban.getBannedUntil().before(Timestamp.valueOf(LocalDateTime.now()))){
                Messages.create("Player " + target.getLastName() + " is not banned at the moment").to(sender).handle();
                return;
            }
            service.remove(ban);
            Messages.create("Player " + target.getLastName() + " has been unbanned").to(sender).handle();
        }catch (RuntimeException re){
            Messages.create("Player " + target.getLastName() + " is not banned at the moment").to(sender).handle();
        }
    }
}
