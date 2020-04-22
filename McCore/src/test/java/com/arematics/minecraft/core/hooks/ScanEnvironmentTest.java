package com.arematics.minecraft.core.hooks;

import org.junit.jupiter.api.Test;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import static org.junit.jupiter.api.Assertions.*;

class ScanEnvironmentTest {

    @Test
    void getBuilder() {
        ConfigurationBuilder builder = ScanEnvironment.getBuilder();

        assertNotNull(builder, "Builder is null");
        assertTrue(builder.getScanners().contains(new TypeAnnotationsScanner()),
                "Builder missing Type Annotation Scanner");
        assertTrue(builder.getScanners().contains(new SubTypesScanner()),
                "Builder missing Sub Type Annotation Scanner");
        assertTrue(builder.getScanners().contains(new MethodAnnotationsScanner()),
                "Builder missing Method Annotation Scanner");
    }
}