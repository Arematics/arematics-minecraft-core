package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component

public class UserUpdateListener implements Listener {

    private final UserService userService;

    @Autowired
    public UserUpdateListener(UserService userService){
        this.userService = userService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        Player player = joinEvent.getPlayer();

        Timestamp current = new Timestamp(System.currentTimeMillis());
        User user = this.userService.getOrCreateUser(player.getUniqueId());
        user.setLastIp(player.getAddress().getAddress().getHostAddress());
        user.setLastIpChange(current);
        user.setLastJoin(current);
        this.userService.update(user);
        ChatAPI.login(player);

        this.userService.update(user);
    }
}
