package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlayerTeleportController {

    // 1. who accepts, 2. who sends
    private final Set<PlayerTeleport> teleports = new HashSet<>();

    public void sendTpaRequest(CorePlayer sender, CorePlayer receiver) {
        if(!teleports.contains(new PlayerTeleport(sender,receiver))) {
            teleports.add(new PlayerTeleport(sender, receiver));
        } else {
            sender.warn("You have already sent the player a teleport request");
        }
    }

    public boolean accept(CorePlayer sender, CorePlayer receiver) {
        for(PlayerTeleport current : teleports) {
            if(current.sender.equals(sender) && current.receiver.equals(receiver)) {
                current.teleportPlayer();
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
                return true;
            }
        }
        return false;
    }


    @Getter
    @RequiredArgsConstructor
    private class PlayerTeleport {
        private final CorePlayer sender;
        private final CorePlayer receiver;

        public void teleportPlayer() {
            ArematicsExecutor.syncRun(() -> sender.getPlayer().teleport(receiver.getPlayer()));
        }

    }
}

