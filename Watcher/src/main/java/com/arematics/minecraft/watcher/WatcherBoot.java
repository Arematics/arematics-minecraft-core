package com.arematics.minecraft.watcher;

import com.arematics.minecraft.core.Bootstrap;

public class WatcherBoot extends Bootstrap {

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public WatcherBoot() {
        super(false);
    }

    @Override
    public void shutdown() { }
}
