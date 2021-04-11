package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.global.model.Mute;
import com.arematics.minecraft.data.service.MuteService;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Perm(permission = "team.punishment.chat.mute", description = "Mute a player")
public class MuteCommand extends CoreCommand {

    private final MuteService muteService;

    @Autowired
    public MuteCommand(MuteService muteService){
        super("mute", true);
        registerLongArgument("reason");
        this.muteService = muteService;
    }

    @SubCommand("{player} {period} {reason}")
    public void mutePlayer(CorePlayer sender, CorePlayer target, Period period, String reason) {
        if(target.getUser().getRank().isInTeam())
            throw new CommandProcessException("Can't mute team members");
        Mute mute = new Mute(target.getUUID(), sender.getUUID(), reason, Timestamp.valueOf(TimeUtils.toLocalDateTime(period)));
        muteService.save(mute);
        sender.info("Player " + target.getName() + " has been muted for " + TimeUtils.toString(period)).handle();
    }

    @SubCommand("unmute {player}")
    public void unmutePlayer(CorePlayer sender, CorePlayer target) {
        try{
            Mute mute = muteService.findMute(target.getUUID());
            muteService.remove(mute);
            sender.info("Unmute player " + target.getName()).handle();
        }catch (Exception e){
            throw new CommandProcessException("Player is not muted");
        }
    }
}
