package com.arematics.minecraft.meta;

import com.arematics.minecraft.core.BaseEngine;
import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.annotations.PluginEngine;

@PluginEngine(dir = "com.arematics.minecraft.meta")
public class MetaEngine extends BaseEngine {

    public MetaEngine(Bootstrap bootstrap) throws Exception {
        super(bootstrap, false);
        System.out.println("Test");
    }

    @Override
    public void shutdown() {
    }
}
