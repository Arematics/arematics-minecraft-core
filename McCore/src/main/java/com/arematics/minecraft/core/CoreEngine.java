package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.messaging.injector.BasicInjector;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;

@PluginEngine(dir = "com.arematics.minecraft.core")
public class CoreEngine extends BaseEngine{

    private final Parser parser = new Parser();
    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     * @param bootstrap JavaPlugin
     */
    public CoreEngine(Bootstrap bootstrap) throws Exception{
        super(bootstrap, true);
    }

    public Parser getParser() {
        return parser;
    }

    public Class<? extends Injector<?>> getDefaultInjectorType() {
        return defaultInjectorType;
    }

    @Override
    public void shutdown() {

    }
}
