package com.arematics.minecraft.clans.listener;

import com.arematics.minecraft.core.bukkit.wrapper.AsyncPlayerDeathEvent;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.service.ClanMemberService;
import com.arematics.minecraft.data.service.ClanService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ClanStatsListener implements Listener {

    private final ClanService clanService;
    private final ClanMemberService clanMemberService;

    @Autowired
    public ClanStatsListener(ClanService clanService, ClanMemberService clanMemberService){
        this.clanService = clanService;
        this.clanMemberService = clanMemberService;
    }

    @EventHandler
    public void deathEvent(AsyncPlayerDeathEvent event){
        patch(event.getPlayer(),
                (clan) -> clan.setDeaths(clan.getDeaths() + 1),
                (member) -> member.setClanDeaths(member.getClanDeaths() + 1));
        patch(event.getKiller(),
                (clan) -> clan.setKills(clan.getKills() + 1),
                (member) -> member.setClanKills(member.getClanKills() + 1));
    }

    private void patch(CorePlayer player, Consumer<Clan> changeClan, Consumer<ClanMember> change){
        try{
            ClanMember member = clanMemberService.getMember(player);
            Clan clan = clanService.findClanById(member.getRank().getClanRankId().getClanId());
            changeClan.accept(clan);
            change.accept(member);
            clanMemberService.update(member);
            clanService.update(clan);
        }catch (RuntimeException ignored){
            ignored.printStackTrace();
        }
    }
}
