package com.arematics.minecraft.watcher;

import com.arematics.minecraft.core.Bootstrap;

public class WatcherBoot extends Bootstrap {

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public WatcherBoot() throws Exception{
        super(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void shutdown() { }
}
