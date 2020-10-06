package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.User;
import com.arematics.minecraft.core.data.service.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;


public class UserUpdateListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        Player player = joinEvent.getPlayer();

        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);

        Timestamp current = new Timestamp(System.currentTimeMillis());
        User user = service.getOrCreateUser(player.getUniqueId());
        user.setLastIp(player.getAddress().getAddress().getHostAddress());
        user.setLastIpChange(current);
        user.setLastJoin(current);
        service.update(user);
        ChatAPI.login(player);

    }
}
