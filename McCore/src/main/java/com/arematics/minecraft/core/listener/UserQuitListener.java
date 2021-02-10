package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserQuitListener implements Listener {

    private final UserService userService;
    private final Tablist tablist;

    @Autowired
    public UserQuitListener(UserService userService, Tablist tablist) {
        this.userService = userService;
        this.tablist = tablist;
    }

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        User user = userService.getUserByUUID(event.getPlayer().getUniqueId());
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(player.isInFight()){
            player.getPlayer().setHealth(0.0D);
        }
        ArematicsExecutor.runAsync(player::updateOnlineTime);
        this.tablist.remove(player);
        this.userService.update(user);
    }
}
