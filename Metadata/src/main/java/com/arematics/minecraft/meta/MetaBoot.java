package com.arematics.minecraft.meta;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.annotations.PluginEngine;

@PluginEngine(dir = "com.arematics.minecraft.meta")
public class MetaBoot extends Bootstrap {

    public MetaBoot() throws Exception {
        super(false);
    }

    @Override
    public void shutdown() {
    }
}
