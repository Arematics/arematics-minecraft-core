package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.ServerPropertiesService;
import com.arematics.minecraft.data.service.UserService;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class UserUpdateListener implements Listener {

    private final Server server;
    private final UserService userService;
    private final ServerPropertiesService serverPropertiesService;
    private final Tablist tablist;

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        server.schedule().runAsync(() -> asyncedJoin(joinEvent.getPlayer()));
    }

    private void asyncedJoin(Player joinPlayer){
        CorePlayer player = server.fetchPlayer(joinPlayer);
        dispatchPlayerData(player);
    }

    private void dispatchPlayerData(CorePlayer player){
        patchUser(player);
        server.schedule().runSync(() -> sendTab(player));
    }

    private void patchUser(CorePlayer player){
        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
        User user = player.getUser();
        user.setLastName(player.getPlayer().getName());
        user.setLastJoin(current);
        this.userService.update(user);
    }

    private void sendTab(CorePlayer player){
        this.tablist.refresh(player);
        TextComponent header = new TextComponent("Arematics presents SoulPvP");
        header.setColor(ChatColor.AQUA);
        try{
            header = new TextComponent(serverPropertiesService.findServerProperties("motd_1").getValue());
        }catch (Exception ignore){}
        header.setBold(true);
        TextComponent footer = new TextComponent("SOULPVP.DE");
        footer.setColor(ChatColor.DARK_GRAY);
        try{
            footer = new TextComponent(serverPropertiesService.findServerProperties("motd_2").getValue());
        }catch (Exception ignore){}
        footer.setBold(true);
        player.getPlayer().setPlayerListHeaderFooter(header, footer);
    }
}
