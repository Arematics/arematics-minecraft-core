package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.tablist.Tablist;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    private final Tablist tablist;

    @Autowired
    public UserUpdateListener(UserService userService, ChatAPI chatAPI, Tablist tablist){
        this.userService = userService;
        this.chatAPI = chatAPI;
        this.tablist = tablist;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        Player player = joinEvent.getPlayer();
        Timestamp current = new Timestamp(System.currentTimeMillis());
        this.tablist.refresh(CorePlayer.get(player));
        User user = this.userService.getOrCreateUser(player.getUniqueId(), player.getName());
        user.setLastName(player.getName());
        user.setLastIp(Md5Crypt.md5Crypt(player.getAddress().getAddress().getHostAddress().getBytes()));
        user.setLastIpChange(current);
        user.setLastJoin(current);
        chatAPI.login(player);
        chatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().add(user);
        this.userService.update(user);

        Messages.create("broadcast_beta_disclaimer")
                .to(Bukkit.getOnlinePlayers().toArray(new CommandSender[]{}))
                .DEFAULT()
                .replace("prefix", "§c§lInfo » §7")
                .disableServerPrefix()
                .handle();
    }
}
