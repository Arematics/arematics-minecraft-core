package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PluginListHiddenListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandSend(PlayerCommandPreprocessEvent e) {



        if(Permissions.hasPermission(CorePlayer.get(e.getPlayer()).getUser(), "chat.bypass")) {return;}


        ArrayList<String> list = new ArrayList<String>();

        list.add("/?");
        list.add("/bukkit:?");
        list.add("/bukkit:pl");
        list.add("/bukkit:plugins");
        list.add("/plugins");
        list.add("/op");
        list.add("/deop");
        list.add("/bukkit:deop");
        list.add("/bukkit:op");
        list.add("/bukkit:help");
        list.add("/minecraft");
        list.add("/me");
        list.add("/bukkit");
        list.add("/about");
        list.add("/tell");
        list.add("/minecraft:tell");
        list.add("/minecraft:me");
        list.add("/pl");

        for(String blocked : list) {

            if(e.getMessage().toLowerCase().contains(blocked)) {
                CorePlayer.get(e.getPlayer()).warn("That command doesn't exist").handle();
                e.setCancelled(true);
                return;
            }
        }
    }
}
