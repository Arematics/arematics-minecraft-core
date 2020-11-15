package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.CompoundClassLoader;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class ScanEnvironment {

    /**
     * Reflections Configuration Builder to add new Urls
     * @return Configuration Builder
     */
    public static ConfigurationBuilder getBuilder(String url, CompoundClassLoader classLoader) {
        return new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(classLoader.getLoaders().toArray(new ClassLoader[]{})))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(url)))
                .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());
    }

    /**
     * Reflections Configuration Builder to add new Urls
     * @return Configuration Builder
     */
    public static ConfigurationBuilder getBuilder(String url, ClassLoader classLoader) {
        return new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(classLoader))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(url)))
                .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());
    }
}
