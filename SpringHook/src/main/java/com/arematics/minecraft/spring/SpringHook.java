package com.arematics.minecraft.spring;

import com.arematics.minecraft.core.*;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class SpringHook extends JavaPlugin {

    @SneakyThrows
    @Override
    public void onEnable() {
        super.onEnable();

        CoreBoot coreBoot = Boots.getBoot(CoreBoot.class);
        List<ClassLoader> classLoaders = Boots.getBoots().keySet()
                .stream()
                .filter(classItem -> classItem != CoreBoot.class)
                .map(Class::getClassLoader)
                .collect(Collectors.toList());
        classLoaders.add(0, coreBoot.getClass().getClassLoader());
        classLoaders.add(1, Thread.currentThread().getContextClassLoader());

        CompoundClassLoader loader = new CompoundClassLoader(classLoaders);
        coreBoot.setContext(SpringSpigotBootstrapper.initialize(coreBoot, loader, Application.class));
        SpringInitializedEvent event = new SpringInitializedEvent(coreBoot.getContext(), coreBoot);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
