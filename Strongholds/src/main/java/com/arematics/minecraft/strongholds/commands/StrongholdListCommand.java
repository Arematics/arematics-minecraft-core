package com.arematics.minecraft.strongholds.commands;

import com.arematics.minecraft.core.annotations.SkipEnum;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.StrongholdTime;
import com.arematics.minecraft.data.service.StrongholdTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
public class StrongholdListCommand extends CoreCommand {

    private final StrongholdTimeService strongholdTimeService;

    @Autowired
    public StrongholdListCommand(StrongholdTimeService strongholdTimeService){
        super("strongholds");
        this.strongholdTimeService = strongholdTimeService;
    }

    @SubCommand("today")
    public void showTodaySchedule(CorePlayer player) {
        sendTimes(player, this.strongholdTimeService.getTodayTimes(TimeUtils.getToday()));
    }

    @SubCommand("{weekday}")
    public void showTodaySchedule(CorePlayer player, @SkipEnum DayOfWeek day) {
        sendTimes(player, this.strongholdTimeService.getTodayTimes(day));
    }

    private void sendTimes(CorePlayer player, List<StrongholdTime> times){
        player.info(CommandUtils.prettyHeader("Stronghold", "Timings")).DEFAULT().disableServerPrefix().handle();
        times.forEach(time -> sendTimeInfo(player, time));
    }

    private void sendTimeInfo(CorePlayer player, StrongholdTime time){
        StringInjector injector = player.info("   §7Time: %time%").DEFAULT();
        LocalTime now = LocalTime.now();
        String color = now.isBefore(time.getTime().toLocalTime()) ? "§a": "§7";
        injector.disableServerPrefix().replace("time", color + time.getTime().toString()).handle();
    }
}
