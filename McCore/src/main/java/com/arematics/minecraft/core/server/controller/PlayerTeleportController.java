package com.arematics.minecraft.core.server.controller;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class PlayerTeleportController {

    // 1. who accepts, 2. who sends
    private final Map<CorePlayer, CorePlayer> tpaRequests = new HashMap<>();

    public void sendTpaRequest(CorePlayer sender, CorePlayer receiver) {
        getTpaRequests().put(receiver, sender);
    }

    public CorePlayer getRequest(CorePlayer receiver) {
        return getTpaRequests().get(receiver);
    }

    public boolean accept(CorePlayer sender, CorePlayer receiver) {
        ArematicsExecutor.syncRun(() -> receiver.getPlayer().teleport(sender.getPlayer()));
        getTpaRequests().remove(receiver);
        return true;
    }

    public void deny(CorePlayer receiver) {
        getTpaRequests().remove(receiver);
    }

}
