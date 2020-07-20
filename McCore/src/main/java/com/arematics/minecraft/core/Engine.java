package com.arematics.minecraft.core;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Engine {

    private static final Map<Class<? extends BaseEngine>, Object> engines = new HashMap<>();

    public static <T> T getEngine(Class<T> engine){
        return engine.cast(engines.getOrDefault(engine, null));
    }
    public static <T extends BaseEngine> void shutdownEngine(Class<T> engine){
        if(engines.containsKey(engine)) getEngine(engine).shutdown();
        else
            Bukkit.getLogger().severe("Engine " + engine.getName() +
                    " Instance not found System shutdown whiteout saving");
    }

    public static void addEngine(BaseEngine engine){
        engines.put(engine.getClass(), engine);
    }
}
