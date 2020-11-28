package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerTeleportController {

    // 1. who accepts, 2. who sends
    private final List<PlayerTeleport> teleports = new ArrayList<>();

    public void sendTpaRequest(CorePlayer sender, CorePlayer receiver) { teleports.add(new PlayerTeleport(sender, receiver)); }

    public List<PlayerTeleport> getRequest(CorePlayer receiver) {
        return teleports;
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

