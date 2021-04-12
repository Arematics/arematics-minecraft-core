package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.service.OnlineTimeService;
import com.arematics.minecraft.data.share.model.OnlineTime;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Setter
@Getter
public class OnlineTimeHandler {

    private static OnlineTimeService onlineTimeService;
    private final CorePlayer player;

    private final LocalDateTime joined;
    private LocalDateTime lastPatch = null;
    private LocalDateTime lastAntiAFKEvent;

    private Duration lastAfk = null;

    public OnlineTimeHandler(CorePlayer player){
        this.player = player;
        this.joined = LocalDateTime.now();
        this.lastAntiAFKEvent = this.joined;
        if(onlineTimeService == null)
            onlineTimeService = Boots.getBoot(CoreBoot.class).getContext().getBean(OnlineTimeService.class);

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
        if(lastPatch == null) lastPatch = player.getUser().getLastJoin().toLocalDateTime();
        Duration online = Duration.between(this.lastPatch, LocalDateTime.now());
        updateOnlineTimeData(false, (time) -> updatePlayedTime(time, online));
        updateOnlineTimeData(true, (time) -> updatePlayedTime(time, online));
        lastPatch = LocalDateTime.now();
    }


    /**
     * Update Players Play Time Data
     */
    private void updatePlayedTime(OnlineTime time, Duration online){
        long totalTime = time.getTime() + online.toMillis();
        time.setTime(totalTime - (this.lastAfk.isNegative() ? 0 : this.lastAfk.toMillis()));
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

    /**
     * Called if Anti AFK Event is executed updating lastAntiAfk Time
     */
    public void callAntiAfk(){
        this.lastAntiAFKEvent = LocalDateTime.now();
    }
}
