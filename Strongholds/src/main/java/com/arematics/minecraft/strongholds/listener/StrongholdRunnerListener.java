package com.arematics.minecraft.strongholds.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.service.ClanMemberService;
import com.arematics.minecraft.data.service.ClanService;
import com.arematics.minecraft.strongholds.capture.controller.StrongholdCaptureController;
import com.arematics.minecraft.strongholds.capture.model.StrongholdCapture;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdCaptureLeaveEvent;
import com.arematics.minecraft.strongholds.events.StrongholdEnterEvent;
import com.arematics.minecraft.strongholds.events.StrongholdLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrongholdRunnerListener implements Listener {

    private final ClanMemberService clanMemberService;
    private final ClanService clanService;
    private final StrongholdCaptureController controller;

    @Autowired
    public StrongholdRunnerListener(ClanMemberService clanMemberService,
                                    ClanService clanService,
                                    StrongholdCaptureController strongholdCaptureController){
        this.clanMemberService = clanMemberService;
        this.clanService = clanService;
        this.controller = strongholdCaptureController;
    }

    @EventHandler
    public void onStrongholdEnter(StrongholdEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCapture capture = this.controller.getCapture();

        if(capture != null && capture.getStronghold().equals(stronghold)) {
            capture.getInStronghold().add(player);
        }
    }

    @EventHandler
    public void onStrongholdLeave(StrongholdLeaveEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCapture capture = this.controller.getCapture();

        if(capture != null && capture.getStronghold().equals(stronghold))
            capture.getInStronghold().remove(player);
    }

    @EventHandler
    public void onCaptureEnter(StrongholdCaptureEnterEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCapture capture = this.controller.getCapture();

        if(capture != null && capture.getStronghold().equals(stronghold)){
            try{
                ClanMember member = this.clanMemberService.getMember(player);
                Clan clan = member.getClan(this.clanService);
                if(!capture.getTimings().containsKey(clan))
                    capture.getTimings().put(clan, 0L);
            }catch (RuntimeException re){
                player.warn("You doesn't have a clan, no capture available").handle();
            }
        }
    }

    @EventHandler
    public void onStrongholdLeave(StrongholdCaptureLeaveEvent event){
        CorePlayer player = event.getPlayer();
        Stronghold stronghold = event.getStronghold();

        StrongholdCapture capture = this.controller.getCapture();

        if(capture != null && capture.getStronghold().equals(stronghold))
            capture.getInStrongholdCapture().remove(player);
    }
}
