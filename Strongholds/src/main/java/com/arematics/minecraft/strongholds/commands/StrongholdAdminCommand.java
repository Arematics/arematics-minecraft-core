package com.arematics.minecraft.strongholds.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.ShTimeId;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.mode.model.StrongholdTime;
import com.arematics.minecraft.data.service.StrongholdTimeService;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;

@Component
@Perm(permission = "clans.stronghold.admin", description = "Permission for full stronghold administration")
public class StrongholdAdminCommand extends CoreCommand {

    private final StrongholdTimeService strongholdTimeService;
    private final StrongholdCaptureController controller;

    @Autowired
    public StrongholdAdminCommand(StrongholdTimeService strongholdTimeService,
                                  StrongholdCaptureController strongholdCaptureController){
        super("shadmin", "shmgr", "sha");
        this.strongholdTimeService = strongholdTimeService;
        this.controller = strongholdCaptureController;
    }

    @SubCommand("enable {stronghold}")
    @Perm(permission = "enable", description = "Permission to enable stronghold")
    public void enableStronghold(CorePlayer player, Stronghold stronghold) {
        boolean success = this.controller.enableStronghold(stronghold);
        if(success) player.info("Stronghold has been enabled").handle();
        else player.warn("Other stronghold is active").handle();
    }

    @SubCommand("disable")
    @Perm(permission = "disable", description = "Permission to disable stronghold")
    public void disableStronghold(CorePlayer player) {
        boolean success = this.controller.disableStronghold();
        if(success) player.info("Stronghold has been disabled").handle();
        else player.warn("No stronghold is active").handle();
    }

    @SubCommand("setActiveTimes {stronghold} {days} {hours:minutes}")
    @Perm(permission = "active", description = "Permission to set active stronghold times")
    public void setActiveTimes(CorePlayer player, Stronghold stronghold, String dayString, Time time) {
        try{
            List<DayOfWeek> days = TimeUtils.fromDaysString(dayString);
            days.forEach(day -> updateDay(stronghold, day, time));
            player.info("Stronghold times updated").handle();
        }catch (Exception e){
            e.printStackTrace();
            throw new CommandProcessException("Day list is not correct formed");
        }
    }

    @SubCommand("clearTimes {stronghold}")
    @Perm(permission = "clear", description = "Permission to clear stronghold times")
    public void clearStrongholdTimes(CorePlayer player, Stronghold stronghold) {
        this.strongholdTimeService.clearAllForStronghold(stronghold);
        player.warn("Stronghold times for " + stronghold.getId() + " cleared").handle();
    }

    private void updateDay(Stronghold stronghold, DayOfWeek dayOfWeek, Time time){
        boolean exists = this.strongholdTimeService.isTimeUsed(dayOfWeek, time);
        if(!exists){
            StrongholdTime timeEntry;
            try{
                ShTimeId id = new ShTimeId(stronghold.getId(), dayOfWeek);
                timeEntry = this.strongholdTimeService.findById(id);
                timeEntry.setTime(time);
            }catch (RuntimeException re){
                timeEntry = new StrongholdTime(stronghold.getId(), dayOfWeek, time);
            }
            this.strongholdTimeService.update(timeEntry);
        }
    }
}
