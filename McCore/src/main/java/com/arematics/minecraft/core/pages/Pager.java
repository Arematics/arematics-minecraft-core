package com.arematics.minecraft.core.pages;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager {

    private final static Map<CommandSender, Pager> pagers = new HashMap<>();

    public static Pager of(CommandSender sender){
        if(!pagers.containsKey(sender))
            pagers.put(sender, new Pager(sender));
        return pagers.get(sender);
    }

    public static void sendDefaultPageMessage(CommandSender sender){
        Messages.create("%before% | %next%")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("before", "Before")
                .setColor(JsonColor.DARK_RED)
                .setHover(HoverAction.SHOW_TEXT, "Page before")
                .setClick(ClickAction.RUN_COMMAND, "/page BEFORE")
                .END()
                .replace("next", "Next")
                .setColor(JsonColor.DARK_GREEN)
                .setHover(HoverAction.SHOW_TEXT, "Next Page")
                .setClick(ClickAction.RUN_COMMAND, "/page NEXT")
                .END()
                .handle();
    }

    private CommandSender sender;
    private Pageable last;
    private final Map<String, Pageable> pageables = new HashMap<>();

    private Pager(CommandSender sender){
        this.sender = sender;
    }

    public Pageable fetch(String key){
        if(isToOld(key)) return null;
        Pageable last = pageables.get(key);
        this.last = last;
        return last;
    }

    public Pageable create(String key, String bindedCommand, List<String> content){
        pageables.put(key, new Pageable(content, sender, bindedCommand));
        return fetch(key);
    }

    public boolean isToOld(String key){
        if(!pageables.containsKey(key)) return false;
        Pageable pageable = pageables.get(key);
        if((System.currentTimeMillis()) - (pageable.getLastUse() + (1000*60)) > 0){
            pageables.remove(key);
            return true;
        }
        return false;
    }

    public Pageable last(){
        return this.last;
    }
}
