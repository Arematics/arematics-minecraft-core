package com.arematics.minecraft.core.chat;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Messenger {

    private final HashMap<CorePlayer, CorePlayer> lastMessenger = new HashMap<>();


    public Messenger() {
    }

    public void sendMsg(CorePlayer player, CorePlayer target, String message) {
        if(player == target) {
            return;
        }
        lastMessenger.put(player, target);
        message(player, target, message);
    }

    public void reply(CorePlayer player, String message) {
        CorePlayer target = lastMessenger.get(player);
        if(target != null) {
           message(player, target, message);
        }
    }

    private void message(CorePlayer player, CorePlayer target, String message) {
        player.info("§8<§bMSG§8> §7Du §8» §b" + target.getPlayer().getDisplayName() + "§7: §f" + message).DEFAULT().disableServerPrefix().handle();
        target.info("§8<§bMSG§8> §b" + player.getPlayer().getDisplayName() + " §8» §7Dir: §f" + message).DEFAULT().disableServerPrefix().handle();
        target.getRequestSettings().addTimeout(player.getPlayer().getName());
    }

}
