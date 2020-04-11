package com.arematics.minecraft.core.hooks;

import org.bukkit.plugin.java.JavaPlugin;

public class MultiHook {

    public static void addHooks(String url, ClassLoader loader, JavaPlugin bootstrap){
        PreFileExistHook.checkPluginFiles(loader, bootstrap);
        CommandHooks.hookCommands(url, loader, bootstrap);
        ListenerHook.hookListeners(url, loader, bootstrap);
        LanguageHook.addLanguageFiles(bootstrap);
    }
}
