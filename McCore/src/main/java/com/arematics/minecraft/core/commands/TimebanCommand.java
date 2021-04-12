package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.global.model.Ban;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.BanService;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Perm(permission = "team.punishment.tban", description = "Ban a player temporary")
public class TimebanCommand extends CoreCommand {
    private final BanService service;
    private final UserService userService;

    @Autowired
    public TimebanCommand(BanService banService, UserService userService){
        super("tban", "time-ban");
        registerLongArgument("reason");
        this.service = banService;
        this.userService = userService;
    }

    @SubCommand("{player} {time} {reason} ")
    public void banPlayer(CorePlayer player, User target, Period period, String reason) {
        if(!player.getUser().getRank().isOver(target.getRank())){
            player.warn("Player " + target.getLastName() + " could not be banned").handle();
            return;
        }
        Ban ban;
        try{
            ban = service.getBan(target.getUuid());
            if(ban.getBannedUntil() == null || ban.getBannedUntil().after(Timestamp.valueOf(LocalDateTime.now()))) {
                player.warn("Player " + target.getLastName() + " is currently banned").handle();
                return;
            }
            ban.setBannedUntil(Timestamp.valueOf(TimeUtils.toLocalDateTime(period)));
        }catch (RuntimeException re){
            ban = new Ban(target.getUuid(), player.getUUID(), reason, Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(TimeUtils.toLocalDateTime(period)));
        }
        Player targetPlayer = Bukkit.getPlayer(target.getUuid());
        Ban finalBan = ban;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        ArematicsExecutor.syncRun(() -> {
            if(targetPlayer != null) targetPlayer.kickPlayer("§cYou have been banned until \n§c" +
                    formatter.format(finalBan.getBannedUntil().toLocalDateTime()) + "\n§b" + finalBan.getReason());
        });
        String msg = "Player " + target.getLastName() + " has been banned until " +
                formatter.format(finalBan.getBannedUntil().toLocalDateTime()) + " by " + player.getName() + " for " +
                reason;
        server.getOnlineTeam().forEach(team -> team.info(msg).handle());
        service.save(ban);
        int days = (int) period.toStandardDuration().getMillis() / 1000 / 60 / 60 / 24;
        target.setKarma(target.getKarma() - (days * 5));
        userService.update(target);
    }
}
