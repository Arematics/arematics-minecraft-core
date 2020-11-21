package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
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

    @Autowired
    public UserQuitListener(UserService userService) {
        this.userService = userService;
    }

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        User user = userService.getUserByUUID(event.getPlayer().getUniqueId());
        ChatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().remove(user);
        this.userService.update(user);
    }
}
