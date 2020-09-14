package com.arematics.minecraft.spring;

import com.arematics.minecraft.core.*;
import com.arematics.minecraft.meta.MetaBoot;
import com.arematics.minecraft.ui.UIBoot;
import com.arematics.minecraft.watcher.WatcherBoot;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Arrays;

public class SpringHook extends JavaPlugin {

    @SneakyThrows
    @Override
    public void onEnable() {
        super.onEnable();

        CoreBoot coreBoot = Boots.getBoot(CoreBoot.class);
        CompoundClassLoader loader = new CompoundClassLoader(coreBoot.getClass().getClassLoader(),
                Thread.currentThread().getContextClassLoader(), UIBoot.class.getClassLoader(),
                WatcherBoot.class.getClassLoader(), MetaBoot.class.getClassLoader());
        coreBoot.setContext(SpringSpigotBootstrapper.initialize(coreBoot, loader, Application.class));
        Arrays.stream(coreBoot.getContext().getBeanDefinitionNames()).forEach(System.out::println);
    }
}
