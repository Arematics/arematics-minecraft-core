package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import org.bukkit.Bukkit;

import java.util.Map;

@PluginEngine(dir = "com.arematics.minecraft.core")
public class CoreEngine extends BaseEngine{

    private static Map<String, CoreEngine> engines;

    private final Parser parser;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     * @param bootstrap JavaPlugin
     */
    public CoreEngine(Bootstrap bootstrap) throws Exception{
        super(bootstrap, true);
        this.parser = new Parser();
    }

    public Parser getParser() {
        return parser;
    }

    @Override
    public void shutdown() {

    }
}
