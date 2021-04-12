package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.listener.Quitable;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@EqualsAndHashCode
public class PlayerTeleportController implements Quitable {

    // 1. who accepts, 2. who sends
    private final BiMap<CorePlayer, CorePlayer> teleports = HashBiMap.create();

    public void sendTpaRequest(CorePlayer sender, CorePlayer receiver) {
        if(!teleports.containsKey(receiver)) {
            teleports.put(receiver, sender);
            receiver.requests().addTimeout(sender.getPlayer().getName());
            ArematicsExecutor.asyncDelayed(() -> teleports.remove(receiver), 1, TimeUnit.MINUTES);
        } else {
            sender.warn("Player has an teleport request at the moment").handle();
        }
    }

    public boolean accept(CorePlayer sender, CorePlayer receiver) {
        if(teleports.containsKey(receiver) && teleports.get(receiver).equals(sender)) {
            sender.teleport(receiver.getLocation()).schedule();
            teleports.remove(receiver);
            return true;
        }
        return false;
    }

    public boolean deny(CorePlayer sender, CorePlayer receiver) {
        if(teleports.containsKey(receiver) && teleports.get(receiver).equals(sender)) {
            teleports.remove(receiver, sender);
            return true;
        }
        return false;
    }

    @Override
    public void quit(Player player) {
        CorePlayer result = CorePlayer.get(player);
        teleports.remove(result);
        teleports.inverse().remove(result);
    }
}

