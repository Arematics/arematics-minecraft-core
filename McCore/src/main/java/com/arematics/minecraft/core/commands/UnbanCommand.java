package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Ban;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.BanService;
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
    public void unbanPlayer(CorePlayer sender, User target) {
        try{
            Ban ban = service.getBan(target.getUuid());
            if(ban.getBannedUntil() != null && ban.getBannedUntil().before(Timestamp.valueOf(LocalDateTime.now()))){
                sender.warn("Player " + target.getLastName() + " is not banned at the moment").handle();
                return;
            }
            service.remove(ban);
            sender.info("Player " + target.getLastName() + " has been unbanned").handle();
        }catch (RuntimeException re){
            sender.warn("Player " + target.getLastName() + " is not banned at the moment").handle();
        }
    }
}
