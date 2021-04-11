package com.arematics.minecraft.strongholds.capture.controller;

import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.mode.model.StrongholdTime;
import com.arematics.minecraft.data.service.StrongholdService;
import com.arematics.minecraft.data.service.StrongholdTimeService;
import com.arematics.minecraft.strongholds.capture.model.StrongholdCapture;
import lombok.Data;
import org.bukkit.Bukkit;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Controller
public class StrongholdCaptureController {

    private StrongholdCapture capture;
    private final StrongholdService strongholdService;
    private final StrongholdTimeService strongholdTimeService;

    @Autowired
    public StrongholdCaptureController(StrongholdService strongholdService,
                                       StrongholdTimeService strongholdTimeService){
        this.strongholdService = strongholdService;
        this.strongholdTimeService = strongholdTimeService;
        this.strongholdTimeService.getTodayTimes(TimeUtils.getToday()).forEach(this::startTimer);
    }

    private void startTimer(StrongholdTime time){
        Stronghold stronghold = this.strongholdService.findById(time.getStrongholdName());
        Duration duration = calculateDuration(time.getTime());
        Bukkit.getLogger().info("Stronghold " + time.getStrongholdName() + " will be activated at: "
                + time.getTime().toString());
        ArematicsExecutor.asyncDelayed(() -> enableStronghold(stronghold), duration.toMinutes(), TimeUnit.MINUTES);
    }

    private Duration calculateDuration(Time time){
        return Duration.between(LocalTime.now(), time.toLocalTime());
    }

    public boolean enableStronghold(Stronghold stronghold){
        if(capture != null) return false;
        this.capture = new StrongholdCapture(stronghold, System.currentTimeMillis() + (1000*60*15));
        this.initRunner();
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .forEach(player -> player.info("Stronghold " + stronghold.getId() + " has been enabled").handle());
        return true;
    }

    public boolean disableStronghold(){
        if(capture == null) return false;
        disable();
        return true;
    }

    public void initRunner(){
        ArematicsExecutor.asyncRepeat(this::updateStronghold, 1, 1,
                TimeUnit.SECONDS, 60*15);
    }

    private void updateStronghold(int timing){
        if(timing == 0){
            disable();
        }else{
            updateClanTimes();
            String time = TimeUtils.toShortString(Period.seconds(timing).normalizedStandard());
            List<Clan> topList = this.getCaptureSortedBest();
            capture.getInStronghold().forEach(player -> updateScoreboard(player, time, topList));
        }
    }

    private void disable(){
        List<Clan> sortedClans = getCaptureSortedList();
        String msg = sortedClans.size() > 0 ? "The clan " + sortedClans.get(0).getTag() + " won" : "No clan has won";
        Messages.create("Stronghold " + capture.getStronghold().getId() + " has been ended")
                .broadcast()
                .handle();
        Messages.create(msg)
                .broadcast()
                .handle();
        capture.getInStronghold().forEach(player -> updateScoreboard(player, "§fNot active", null));
        this.capture = null;
    }

    private void updateClanTimes(){
        Set<Map.Entry<Clan, Long>> captures = capture.getTimings().entrySet();
        for(Map.Entry<Clan, Long> entry : captures){
            if(checkClanOnlines(entry.getKey())) {
                long val = entry.getValue();
                capture.getTimings().remove(entry.getKey());
                capture.getTimings().put(entry.getKey(), val + 1000);
            }
        }
    }

    private boolean checkClanOnlines(Clan clan){
        return clan.getAllOnline().anyMatch(player -> this.capture.getInStrongholdCapture().contains(player));
    }

    private void updateScoreboard(CorePlayer player, String content, List<Clan> topList){
        ArematicsExecutor.syncRun(() -> updateCaptureTime(player, "stronghold-capture", content));
        ArematicsExecutor.syncRun(() -> updateCaptureTime(player, "stronghold", content));
        if(topList != null){
            ArematicsExecutor.syncRun(() -> updateTopList(player, "stronghold-capture", topList));
            ArematicsExecutor.syncRun(() -> updateTopList(player, "stronghold", topList));
        }
    }

    private void updateCaptureTime(CorePlayer player, String id, String content){
        BoardHandler handler = player.getBoard().getBoard(id);
        if(handler != null && handler.isShown())
            handler.setEntrySuffix("Time", content);
    }

    private void updateTopList(CorePlayer player, String id, List<Clan> topList){
        BoardHandler handler = player.getBoard().getBoard(id);
        if(handler != null && handler.isShown()) {
            for(int i = 0; i < topList.size(); i++)
                handler.setEntrySuffix("Place " + (i + 1), "§a" + topList.get(i).getTag());
        }
    }

    private List<Clan> getCaptureSortedList(){
        return capture.getTimings().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .sorted(Collections.reverseOrder())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Clan> getCaptureSortedBest(){
        return capture.getTimings().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
