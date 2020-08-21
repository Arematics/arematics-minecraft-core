package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;

@PluginEngine(dir = "com.arematics.minecraft.core")
public class CoreBoot extends Bootstrap{

    private final Parser parser = new Parser();
    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public CoreBoot() throws Exception{
        super(true);
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
