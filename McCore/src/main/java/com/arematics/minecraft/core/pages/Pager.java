package com.arematics.minecraft.core.pages;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Pager {

    public static void sendDefaultPageMessage(CorePlayer player, String key){
        key = bindedCommandToKey(key);
        Pageable pageable = player.getPager().fetch(key);
        if(pageable.hasNext() || pageable.hasBefore())
        Messages.create("§7<------ %before% | %next% §7------>")
                .to(player.getPlayer())
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("before", new Part("Before")
                        .setBaseColor(JsonColor.DARK_RED)
                        .setHoverAction(HoverAction.SHOW_TEXT, "Page before")
                        .setClickAction(ClickAction.RUN_COMMAND, "/page BEFORE " + key))
                .replace("next", new Part("Next")
                        .setBaseColor(JsonColor.DARK_GREEN)
                        .setHoverAction(HoverAction.SHOW_TEXT, "Next Page")
                        .setClickAction(ClickAction.RUN_COMMAND, "/page NEXT " + key))
                .handle();
    }

    private static String bindedCommandToKey(String bindedCommand){
        return bindedCommand.replaceAll(" ", "");
    }

    private final CorePlayer player;
    private Pageable last;
    private final Map<String, Pageable> pageables = new HashMap<>();

    public Pager(CorePlayer player){
        this.player = player;
    }

    public Pageable fetch(String key){
        key = Pager.bindedCommandToKey(key);
        if(isToOld(key)) return null;
        Pageable last = pageables.get(key);
        this.last = last;
        return last;
    }

    public Pageable fetchOrCreate(String key, Function<CorePlayer, List<String>> values){
        if(isToOld(Pager.bindedCommandToKey(key)))
            pageables.put(Pager.bindedCommandToKey(key), new Pageable(values.apply(this.player), player, key,
                    120));
        Pageable last = pageables.get(Pager.bindedCommandToKey(key));
        this.last = last;
        return last;
    }

    public Pageable create(String bindedCommand, List<String> content){
        pageables.put(Pager.bindedCommandToKey(bindedCommand),
                new Pageable(content, player, bindedCommand, 120));
        return fetch(Pager.bindedCommandToKey(bindedCommand));
    }

    public Pageable create(String bindedCommand, List<String> content, int maxCacheSeconds){
        pageables.put(Pager.bindedCommandToKey(bindedCommand),
                new Pageable(content, player, bindedCommand, maxCacheSeconds));
        return fetch(Pager.bindedCommandToKey(bindedCommand));
    }

    public boolean isToOld(String key){
        if(!pageables.containsKey(key)) return true;
        Pageable pageable = pageables.get(key);
        if((System.currentTimeMillis()) - (pageable.getLastUse() + (1000L * pageable.getMaxCacheSeconds())) > 0){
            pageables.remove(key);
            return true;
        }
        return false;
    }

    public Pageable last(){
        return this.last;
    }

    public void unload(){
        this.pageables.values().forEach(value -> value = null);
    }
}
