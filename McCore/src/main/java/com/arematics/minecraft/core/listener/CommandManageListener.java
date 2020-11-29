package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;
import org.springframework.stereotype.Component;

@Component
public class CommandManageListener implements Listener {

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent e) {

        if(!(e.isCancelled())){
            Player p = e.getPlayer();
            String msg = e.getMessage().split(" ")[0];
            HelpTopic topic = Bukkit.getHelpMap().getHelpTopic(msg);
            if(topic == null){
                CorePlayer.get(p).warn("That command doesn't exist").handle();
                e.setCancelled(true);
            }
        }

    }
}
