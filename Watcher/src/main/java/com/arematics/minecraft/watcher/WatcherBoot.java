package com.arematics.minecraft.watcher;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.annotations.PluginEngine;

@PluginEngine(dir = "com.arematics.minecraft.watcher")
public class WatcherBoot extends Bootstrap {

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public WatcherBoot() throws Exception{
        super(false);
    }

    @Override
    public void shutdown() {

    }
}
