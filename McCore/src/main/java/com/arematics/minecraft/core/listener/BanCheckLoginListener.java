package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.data.global.model.Ban;
import com.arematics.minecraft.data.service.BanService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BanCheckLoginListener implements Listener {

    private final BanService service;

    @Autowired
    public BanCheckLoginListener(BanService banService){
        this.service = banService;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        try{
            Ban ban = service.getBan(event.getPlayer().getUniqueId());
            if(ban.getBannedUntil() == null){
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cYou have been banned permanent \n§b" + ban.getReason());
            }
            if(ban.getBannedUntil().after(Timestamp.valueOf(LocalDateTime.now()))){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cYou have been banned until \n§c" +
                        formatter.format(ban.getBannedUntil().toLocalDateTime()) + "\n§b" + ban.getReason());
            }
        }catch (RuntimeException ignored){}
    }
}
