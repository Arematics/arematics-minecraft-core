package com.arematics.minecraft.core;

import org.bukkit.Server;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

import java.util.UUID;

public class TestEnvironment {

    public static Server server;
    public static World testWorld1;

    @BeforeAll
    public static void setup() {
        server = Mockito.mock(Server.class);
        Mockito.when(server.getName()).thenReturn("TESTSERVER");
        Mockito.when(server.getVersion()).thenReturn("ARESPIGOT-1.8.8");
        testWorld1 = Mockito.mock(World.class);
        Mockito.when(testWorld1.getName()).thenReturn("testwelt");
        Mockito.when(testWorld1.getUID()).thenReturn(UUID.randomUUID());
    }
}
