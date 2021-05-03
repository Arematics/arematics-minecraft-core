package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.service.OnlineTimeService;
import com.arematics.minecraft.data.share.model.OnlineTime;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Setter
@Getter
public class OnlineTimeHandler extends PlayerHandler{

    private final OnlineTimeService onlineTimeService;
    private final LocalDateTime joined;
    private LocalDateTime lastPatch;
    private LocalDateTime lastAntiAFKEvent;

    private Duration lastAfk = null;

    @Autowired
    public OnlineTimeHandler(OnlineTimeService onlineTimeService){
        this.onlineTimeService = onlineTimeService;
        this.joined = LocalDateTime.now();
        this.lastPatch = this.joined;
        this.lastAntiAFKEvent = this.joined;

        this.updateOnlineTimeData(true, OnlineTime::getTime);
        this.updateOnlineTimeData(false, OnlineTime::getTime);
    }

    /**
     * Update Players Afk Time Data
     */
    private void updateAfkTime(){
        this.lastAfk = Duration.between(this.lastAntiAFKEvent.plusMinutes(1), LocalDateTime.now());
        this.lastAntiAFKEvent = LocalDateTime.now();
        if(lastAfk.isNegative()) return;
        updateOnlineTimeData(false, (time) -> time.setAfk(time.getAfk() + lastAfk.toMillis()));
        updateOnlineTimeData(true, (time) -> time.setAfk(time.getAfk() + lastAfk.toMillis()));
    }


    /**
     * Update Players OnlineTime Data
     */
    public void updateOnlineTime(){
        this.updateAfkTime();
        Duration online = Duration.between(this.lastPatch, LocalDateTime.now());
        updateOnlineTimeData(false, (time) -> updatePlayedTime(time, online));
        updateOnlineTimeData(true, (time) -> updatePlayedTime(time, online));
        this.lastPatch = LocalDateTime.now();
    }


    /**
     * Update Players Play Time Data
     */
    private void updatePlayedTime(OnlineTime time, Duration online){
        long totalTime = time.getTime() + online.toMillis();
        time.setTime(totalTime);
    }

    public void updateOnlineTimeData(boolean mode, Consumer<OnlineTime> update){
        OnlineTime time;
        try{
            time = mode ? OnlineTimeHandler.onlineTimeService.findByModeUUID(player.getUUID()) :
                    OnlineTimeHandler.onlineTimeService.findByGlobalUUID(player.getUUID());
        }catch (RuntimeException re){
            time = new OnlineTime(player.getUUID(), 0L, 0L);
        }
        update.accept(time);
        if(mode)
            OnlineTimeHandler.onlineTimeService.putMode(time);
        else
            OnlineTimeHandler.onlineTimeService.putGlobal(time);
    }

    public Period fetchOnlineTimeData(boolean mode, Function<OnlineTime, Period> update){
        OnlineTime time;
        try{
            time = mode ? OnlineTimeHandler.onlineTimeService.findByModeUUID(player.getUUID()) :
                    OnlineTimeHandler.onlineTimeService.findByGlobalUUID(player.getUUID());
        }catch (RuntimeException re){
            time = new OnlineTime(player.getUUID(), 0L, 0L);
        }
        return update.apply(time);
    }

    /**
     * Called if Anti AFK Event is executed updating lastAntiAfk Time
     */
    public void callAntiAfk(){
        this.lastAntiAFKEvent = LocalDateTime.now();
    }

    public Period totalTime(boolean mode){
        return fetchOnlineTimeData(mode, (time) -> Period.millis((int) (time.getTime() / 1000)));
    }

    public Period afkTime(boolean mode){
        return fetchOnlineTimeData(mode, (time) -> Period.millis((int) (time.getAfk() / 1000)));
    }

    public Period activeTime(boolean mode){
        return fetchOnlineTimeData(mode, (time) -> Period.millis((int) (time.getTime() - time.getAfk() / 1000)));
    }

    public String totalTimeString(boolean mode){
        return TimeUtils.toString(totalTime(mode));
    }

    public String afkTimeString(boolean mode){
        return TimeUtils.toString(afkTime(mode));
    }

    public String activeTimeString(boolean mode){
        return TimeUtils.toString(activeTime(mode));
    }
}
