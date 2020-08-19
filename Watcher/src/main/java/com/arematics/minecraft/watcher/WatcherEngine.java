package com.arematics.minecraft.watcher;

import com.arematics.minecraft.core.BaseEngine;
import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.annotations.PluginEngine;

@PluginEngine(dir = "com.arematics.minecraft.watcher")
public class WatcherEngine extends BaseEngine {

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     * @param bootstrap JavaPlugin
     */
    public WatcherEngine(Bootstrap<?> bootstrap) throws Exception{
        super(bootstrap, false);
    }

    @Override
    public void shutdown() {

    }
}
