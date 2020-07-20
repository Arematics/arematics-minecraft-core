package com.arematics.minecraft.watcher;

import com.arematics.minecraft.core.BaseEngine;
import com.arematics.minecraft.core.Bootstrap;

public class WatcherBootstrap extends Bootstrap {

    private BaseEngine engine;

    @Override
    public Bootstrap getPlugin() {
        return this;
    }

    @Override
    public BaseEngine getEngine() throws Exception {
        if(engine != null) return engine;
        return new WatcherEngine(this);
    }
}
