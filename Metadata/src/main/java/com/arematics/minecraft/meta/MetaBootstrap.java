package com.arematics.minecraft.meta;

import com.arematics.minecraft.core.BaseEngine;
import com.arematics.minecraft.core.Bootstrap;

public class MetaBootstrap extends Bootstrap {

    private BaseEngine engine;

    @Override
    public Bootstrap getPlugin() {
        return this;
    }

    @Override
    public BaseEngine getEngine() throws Exception {
        if(engine != null) return engine;
        return engine = new MetaEngine(this);
    }
}
