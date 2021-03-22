package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.AccountLink;
import com.arematics.minecraft.data.service.AccountLinkService;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AccountLinkListener implements Listener {

    private final AccountLinkService accountLinkService;

    private final Map<String, CorePlayer> userMap = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        String ip = player.getPlayer().getAddress().getAddress().getHostAddress();
        if(!userMap.containsKey(ip)){
            userMap.put(ip, player);
        }else{
            CorePlayer from = userMap.get(ip);
            if(!from.getUUID().equals(player.getUUID())) {
                if (!accountLinkService.isLinkExistsBoth(from.getUUID(), player.getUUID())) {
                    AccountLink link = new AccountLink(from.getUUID(), player.getUUID(),
                            "System", null);
                    accountLinkService.save(link);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(userMap.containsValue(player)){
            String ip = player.getPlayer().getAddress().getAddress().getHostAddress();
            ArematicsExecutor.asyncDelayed(() -> userMap.remove(ip, player), 10, TimeUnit.MINUTES);
        }
    }
}
