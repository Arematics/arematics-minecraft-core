package com.arematics.minecraft.core.chat;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.RequestHandler;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.model.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Messenger {

    private final Server server;
    private final HashMap<Player, Player> lastMessenger = new HashMap<>();

    public void sendMsg(CorePlayer player, CorePlayer target, String message) {
        if(player == target) {
            return;
        }
        lastMessenger.put(target.getPlayer(), player.getPlayer());
        message(player, target, message);
    }

    public void reply(CorePlayer player, String message) {
        Player result = lastMessenger.get(player.getPlayer());
        if(result != null && result.isOnline()) {
            lastMessenger.put(result, player.getPlayer());
            lastMessenger.put(player.getPlayer(), result);
            message(player, server.players().fetchPlayer(result), message);
        }
    }

    private void message(CorePlayer player, CorePlayer target, String message) {
        player.info("§8<§bMSG§8> §7Du §8» §b" + parseName(target) + "§7: §f" + message).DEFAULT().disableServerPrefix().handle();
        target.info("§8<§bMSG§8> " + parseName(player) + " §8» §7Dir: §f" + message).DEFAULT().disableServerPrefix().handle();
        target.handle(RequestHandler.class).addTimeout(player.getPlayer().getName());
    }

    private String parseName(CorePlayer target){
        return getRank(target.getUser()).getColorCode() + target.getPlayer().getDisplayName();
    }

    private Rank getRank(User user){
        return user.getDisplayRank() != null ? user.getDisplayRank(): user.getRank();
    }
}
