package com.arematics.minecraft.strongholds.capture.controller;

import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.TimeUtils;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.strongholds.capture.model.StrongholdCapture;
import lombok.Data;
import org.joda.time.Period;

import java.util.concurrent.TimeUnit;

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
        Period period = Period.seconds(timing).normalizedStandard();
        capture.getInStronghold().forEach(player -> updateScoreboard(player, period));
    }

    private void updateScoreboard(CorePlayer player, Period period){
        ArematicsExecutor.syncRun(() -> updateBoardIfShown(player, "stronghold-capture", period));
        ArematicsExecutor.syncRun(() -> updateBoardIfShown(player, "stronghold", period));
    }

    private void updateBoardIfShown(CorePlayer player, String id, Period period){
        BoardHandler handler = player.getBoard().getBoard(id);
        if(handler != null && handler.isShown())
            handler.setEntrySuffix("Time", "§a" + TimeUtils.toShortString(period));
    }
}
