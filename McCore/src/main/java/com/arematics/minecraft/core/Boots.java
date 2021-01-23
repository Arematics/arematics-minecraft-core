package com.arematics.minecraft.core;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Boots {

    private static final Map<Class<? extends Bootstrap>, Object> boots = new HashMap<>();

    public static Map<Class<? extends Bootstrap>, Object> getBoots() {
        return boots;
    }

    public static <T> T getBoot(Class<T> boot){
        return boot.cast(boots.getOrDefault(boot, null));
    }
    public static <T extends Bootstrap> void shutdownBoot(Class<T> boot){
        if(boots.containsKey(boot)) getBoot(boot).shutdown();
        else Bukkit.getLogger().severe("Engine " + boot.getName() +
                " Instance not found System shutdown whiteout saving");
    }

    public static void addBoot(Bootstrap boot){
        boots.put(boot.getClass(), boot);
    }
}
