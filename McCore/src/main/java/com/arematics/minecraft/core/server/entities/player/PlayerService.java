package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.data.service.GameStatsService;
import com.arematics.minecraft.data.service.OgService;
import com.arematics.minecraft.data.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerService {

    private final UserService userService;
    private final GameStatsService gameStatsService;
    private final OgService ogService;

    private final Map<UUID, CorePlayer> corePlayerMap = new HashMap<>();
    private final Set<Class<? extends PlayerHandler>> registeredPlayerHandlers = new HashSet<>();

    public void registerHandler(Class<? extends PlayerHandler> handlerClass){
        registeredPlayerHandlers.add(handlerClass);
    }

    public CorePlayer fetchPlayer(Player player){
        if(!corePlayerMap.containsKey(player.getUniqueId()))
            corePlayerMap.put(player.getUniqueId(), new CorePlayer(player,
                    userService,
                    gameStatsService,
                    ogService, registeredPlayerHandlers));
        return corePlayerMap.get(player.getUniqueId());
    }

    public void invalidate(Player player){
        if(corePlayerMap.containsKey(player.getUniqueId())) corePlayerMap.get(player.getUniqueId()).unload();
        corePlayerMap.remove(player.getUniqueId());
    }
}
