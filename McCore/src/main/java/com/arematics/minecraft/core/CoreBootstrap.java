package com.arematics.minecraft.core;

/**
 * Bootstrap Class starting the Java Plugin an sending Startup or Shutdown Message to Engine.
 */
public class CoreBootstrap extends Bootstrap {

    private BaseEngine engine;

    @Override
    public Bootstrap getPlugin() {
        return this;
    }

    @Override
    public BaseEngine getEngine() throws Exception {
        if(engine != null) return engine;
        return new CoreEngine(this);
    }
}
