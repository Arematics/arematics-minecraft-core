package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.listener.Quitable;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerTeleportController implements Quitable {

    private final Server server;
    private final BiMap<CorePlayer, CorePlayer> teleports = HashBiMap.create();

    public boolean sendTpaRequest(CorePlayer sender, CorePlayer receiver) {
        if(!teleports.containsKey(receiver)) {
            teleports.put(receiver, sender);
            receiver.requests().addTimeout(sender.getPlayer().getName());
            server.schedule().asyncDelayed(() -> teleports.remove(receiver), 1, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public boolean accept(CorePlayer sender, CorePlayer receiver) {
        if(teleports.containsKey(receiver) && teleports.get(receiver).equals(sender)) {
            sender.interact().teleport(receiver.getLocation()).schedule();
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
        CorePlayer result = server.fetchPlayer(player);
        teleports.remove(result);
        teleports.inverse().remove(result);
    }
}

