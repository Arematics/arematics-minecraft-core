package com.arematics.minecraft.data.controller;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.GameMap;
import com.arematics.minecraft.data.service.MapService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Controller
public class MapController {

    private String currentMapId;
    private final Set<CorePlayer> voted = new HashSet<>();
    private final Map<String, Integer> votes = new HashMap<>();
    private final List<String> nextMapIds = new ArrayList<>();
    private final MapService service;
    private final SpawnCommand spawnCommand;
    private final SecureRandom random;
    LocalDateTime nextExecute;

    @Autowired
    public MapController(MapService mapService, SpawnCommand spawnCommand){
        this.service = mapService;
        this.spawnCommand = spawnCommand;
        this.random = new SecureRandom();
        List<String> ids = this.service.findAllIds();
        Collections.shuffle(ids);
        this.currentMapId = ids.size() >= 1 ? ids.get(0) : null;
        generateNextIds(ids);
        start();
    }



    public void addVote(CorePlayer player, String map){
        voted.add(player);
        int vote = votes.getOrDefault(map, 0) + 1;
        votes.put(map, vote);
    }

    public String getHighest(){
        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private void generateNextIds(List<String> allIds){
        this.nextMapIds.clear();
        int result = this.random.nextInt(2) + 1;
        if(result == 2) this.nextMapIds.add(this.currentMapId);
        allIds.remove(this.currentMapId);
        Collections.shuffle(allIds);
        if(allIds.size() >= 1) this.nextMapIds.add(allIds.get(0));
        if(allIds.size() >= 2) this.nextMapIds.add(allIds.get(1));
        if(allIds.size() >= 3 && this.nextMapIds.size() == 2) this.nextMapIds.add(allIds.get(2));
    }

    private void start(){
        Duration duration = getDuration();
        nextExecute = LocalDateTime.now().plus(duration);


        if (duration.toMillis() > 120_000) {
            long timeTillFirstExecute = duration.toMillis() - 59_999;
            ArematicsExecutor.asyncDelayed(this::mentionMapChange, timeTillFirstExecute, TimeUnit.MILLISECONDS);
        }

        ArematicsExecutor.asyncDelayed(this::executeMapChange, duration.toMillis() - 5_000, TimeUnit.MILLISECONDS);
    }

    private void mentionMapChange(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Messages.create("map_change_time")
                .to(Bukkit.getOnlinePlayers().toArray(new Player[]{}))
                .DEFAULT()
                .replace("time", dateTimeFormatter.format(nextExecute))
                .handle();
    }

    private void executeMapChange(){
        ArematicsExecutor.syncRepeat(this::mapChange, 0, 1, TimeUnit.SECONDS, 5);
    }

    private void mapChange(int time){
        if (time == 0) {
            ArematicsExecutor.syncRun(this::changeMap);
            ArematicsExecutor.asyncDelayed(this::start, 1, TimeUnit.SECONDS);
        } else {
            Period period = Period.seconds(time);
            Messages.create("map_change_in")
                    .to(Bukkit.getOnlinePlayers().toArray(new Player[]{}))
                    .DEFAULT()
                    .replace("seconds", TimeUtils.toString(period))
                    .handle();
        }
    }

    private void changeMap(){
        String id = getHighest();
        try{
            GameMap map = this.service.findById(id);
            this.currentMapId = id;
            Messages.create("map_change_now")
                    .to(Bukkit.getOnlinePlayers().toArray(new Player[]{}))
                    .DEFAULT()
                    .replace("mapId", id)
                    .handle();
            Bukkit.getOnlinePlayers().forEach(player -> player.teleport(map.getLocation()));
        }catch (Exception e){
            Bukkit.getOnlinePlayers().forEach(player ->
                    player.teleport(this.spawnCommand.getWarpService().getWarp("spawn").getLocation()));
        }
        voted.clear();
        votes.clear();
        List<String> ids = this.service.findAllIds();
        generateNextIds(ids);
        this.getNextMapIds().forEach(mapId -> this.votes.put(mapId, 0));
    }

    private Duration getDuration(){
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextClear = now.plusMinutes(15);

        return Duration.between(now, nextClear);
    }
}
