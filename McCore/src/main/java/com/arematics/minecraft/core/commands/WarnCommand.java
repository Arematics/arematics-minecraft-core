package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.global.model.Warn;
import com.arematics.minecraft.data.service.WarnService;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@Perm(permission = "team.punishment.warn", description = "Warn a player")
public class WarnCommand extends CoreCommand {

    private final WarnService warnService;
    private final TimebanCommand timebanCommand;

    @Autowired
    public WarnCommand(WarnService warnService, TimebanCommand timebanCommand){
        super("warn");
        this.warnService = warnService;
        this.timebanCommand = timebanCommand;
    }

    @SubCommand("{target} {amount} {message}")
    public void subMethodNameReplace(CorePlayer sender, User target, Byte amount, String message) {
        if(!sender.getUser().getRank().isOver(target.getRank()))
            throw new CommandProcessException("Can't warn team members");
        int currentWarns = warnService.getWarnAmount(target.getUuid());
        int after = currentWarns + amount;
        Period banTime = banPeriod(currentWarns, after);
        if(banTime.getDays() > 0 && !sender.hasPermission("team.punishment.warn.ban"))
            throw new CommandProcessException("Player need to be banned by a supporter or mod");
        Warn warn = new Warn(null, target.getUuid(), sender.getUUID(), (int) amount, message,
                Timestamp.valueOf(LocalDateTime.now()));
        warnService.save(warn);
        target.online().ifPresent(player -> player.info("You have been warned " + amount + " times"));
        String msg = "Player " + target.getLastName() + " has been warned " + amount + " times by " + sender.getName() + " for " + message;
        server.getOnlineTeam().forEach(team -> team.info(msg).handle());
        if(sender.hasPermission("team.punishment.warn.ban") && banTime.getDays() > 0)
            timebanCommand.banPlayer(sender, target, banTime, after + " Warns");
    }

    private Period banPeriod(int current, int result){
        Period start = Period.days(0);
        while(current <= result){
            if(current >= 10) start = start.plusDays(7);
            else if(current == 8) start = start.plusDays(5);
            current++;
        }
        return start;
    }
}
