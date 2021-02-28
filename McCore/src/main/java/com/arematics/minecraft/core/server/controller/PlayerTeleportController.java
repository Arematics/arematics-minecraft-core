package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@EqualsAndHashCode
public class PlayerTeleportController {

    // 1. who accepts, 2. who sends
    private final Set<PlayerTeleport> teleports = new HashSet<>();

    public void sendTpaRequest(CorePlayer sender, CorePlayer receiver) {
        if(!teleports.contains(new PlayerTeleport(sender, receiver))) {
            teleports.add(new PlayerTeleport(sender, receiver));
        } else {
            sender.warn("You have already sent the player a teleport request");
        }
    }

    public boolean accept(CorePlayer sender, CorePlayer receiver) {
        for(PlayerTeleport current : teleports) {
            if(current.sender.equals(sender) && current.receiver.equals(receiver)) {
                current.teleportPlayer();
                receiver.getRequestSettings().addTimeout(sender.getPlayer().getName());
                teleports.remove(current);
                return true;
            }
        }
        return false;
    }

    public boolean deny(CorePlayer sender, CorePlayer receiver) {
        for(PlayerTeleport current : teleports) {
            if(current.sender.equals(sender) && current.receiver.equals(receiver)) {
                teleports.remove(current);
                receiver.getRequestSettings().addTimeout(sender.getPlayer().getName());
                return true;
            }
        }
        return false;
    }


    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class PlayerTeleport {
        private final CorePlayer sender;
        private final CorePlayer receiver;

        public void teleportPlayer() {
            sender.teleport(receiver.getLocation()).schedule();
        }

    }
}

