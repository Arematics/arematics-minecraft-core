package com.arematics.minecraft.strongholds.capture.controller;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.strongholds.capture.model.StrongholdCapture;
import lombok.Data;
import org.joda.time.Period;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
public class StrongholdCaptureRunner {

    private final StrongholdCapture capture;

    public StrongholdCaptureRunner(Stronghold stronghold){
        this.capture = new StrongholdCapture(stronghold, System.currentTimeMillis() + (1000*60*15));
    }

    public void initRunner(){
        ArematicsExecutor.asyncRepeat(this::updateStronghold, 1, 1, TimeUnit.SECONDS, 60*15);
    }

    private void updateStronghold(int timing){
        if(timing == 0){
            List<Clan> sortedClans = getCaptureSortedList();
            Messages.create("Stronghold " + capture.getStronghold().getId() + " has been ended")
                    .broadcast()
                    .handle();
            Messages.create("The clan " + sortedClans.get(0).getTag() + " won")
                    .broadcast()
                    .handle();
            capture.getInStronghold().forEach(player -> updateScoreboard(player, "§fNicht aktiv", null));
        }else{
            updateClanTimes();
            String time = TimeUtils.toShortString(Period.seconds(timing).normalizedStandard());
            List<Clan> topList = this.getCaptureSortedBest();
            capture.getInStronghold().forEach(player -> updateScoreboard(player, time, topList));
        }
    }

    private void updateClanTimes(){
        capture.getTimings().entrySet().stream()
                .filter(entry -> checkClanOnlines(entry.getKey()))
                .forEach(entry -> entry.setValue(entry.getValue() + 1000));
    }

    private boolean checkClanOnlines(Clan clan){
        return clan.getAllOnline().anyMatch(player -> this.capture.getInStrongholdCapture().contains(player));
    }

    private void updateScoreboard(CorePlayer player, String content, List<Clan> topList){
        ArematicsExecutor.syncRun(() -> updateCaptureTime(player, "stronghold-capture", content));
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
                handler.setEntrySuffix("Platz " + (i + 1), "§a" + topList.get(i).getTag());
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
                .sorted(Collections.reverseOrder())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
