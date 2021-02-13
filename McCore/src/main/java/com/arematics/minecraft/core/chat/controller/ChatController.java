package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ChatController {

    private final Server server;

    private String chatMessage;

    public void chat(CorePlayer player, String message) {
        setChatMessage(message);
        server.getOnline().forEach(user -> user.info(msg(player, message)).DEFAULT().disableServerPrefix().handle());
    }

    private String msg(CorePlayer player, String message){
        Rank rank = getRank(player.getUser());
        return "§8§l[" + rank.getColorCode() + rank.getName() + "§8§l] §7" + player.getPlayer().getName() + " §8» " + colorCode(player) +
                createChatMessage(player, message);
    }

    private String colorCode(CorePlayer player){
        return player.getUser().getRank().isInTeam() ? "§c" : "§f";
    }

    private String createChatMessage(CorePlayer player, String message){
        return player.hasPermission("chatcolor") ? ChatColor.translateAlternateColorCodes('&', message) : message;
    }

    private Rank getRank(User user){
        return user.getDisplayRank() != null ? user.getDisplayRank(): user.getRank();
    }
}
