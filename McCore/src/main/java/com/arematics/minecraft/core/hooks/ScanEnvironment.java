package com.arematics.minecraft.core.hooks;

import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

public class ScanEnvironment {

    private static ConfigurationBuilder builder = null;

    /**
     * Generates the Reflections Configuration
     */
    public static void generateBuilder(){
        builder = new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
    }

    /**
     * Reflections Configuration Builder to add new Urls
     * @return Configuration Builder
     */
    public static ConfigurationBuilder getBuilder() {
        if(builder == null) System.out.println("TF");
        return builder;
    }
}
