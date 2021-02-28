package com.arematics.minecraft.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WhitelistTurnDownListener implements Listener {
    
    List<String> players = new ArrayList<String>(){{
       add("_Monty_");
       add("Hecamus");
       add("marinus1111");
       add("Tastentelefon");
       add("Ezgimo");
       add("Zirchdi");
       add("LucyMarryMe");
       add("Z1B4");
       add("jeck14");
       add("BOSSBIITCHSTICKI");
       add("RacePvP");
       add("EP1X");
       add("TheseLions");
       add("Koboo");
       add("McMeze");
       add("GetAim_");
       add("Leckomio1232");
       add("Tobi15X");
    }};

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        if(players.contains(event.getPlayer().getName())) event.getPlayer().setWhitelisted(true);
    }
}
