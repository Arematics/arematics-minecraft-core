package com.arematics.minecraft.core.listener;

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
    private final ChatAPI chatAPI;

    @Autowired
    public UserUpdateListener(UserService userService, ChatAPI chatAPI){
        this.userService = userService;
        this.chatAPI = chatAPI;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        Player player = joinEvent.getPlayer();
        Timestamp current = new Timestamp(System.currentTimeMillis());
        User user = this.userService.getOrCreateUser(player.getUniqueId(), player.getName());
        user.setLastName(player.getName());
        user.setLastIp(player.getAddress().getAddress().getHostAddress());
        user.setLastIpChange(current);
        user.setLastJoin(current);
        chatAPI.login(player);
        chatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().add(user);
        this.userService.update(user);
    }
}
