package com.arematics.minecraft.core;

import com.arematics.minecraft.data.global.model.Ban;
import com.arematics.minecraft.data.mode.model.Warp;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BukkitSerializerTest extends TestEnvironment{

    private Object object;
    private Object moreComplexObject;
    private BukkitSerializer serializer;

    @BeforeEach
    public void eachSetup(){
        serializer = new BukkitSerializer();
        object = new Ban(UUID.randomUUID(),
                UUID.randomUUID(),
                "1",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now().plusHours(5)));
        moreComplexObject = new Warp("abc", new Location(testWorld1, 20, 20, 20, 1.5f, 2.3f));
    }

    @Test
    void testNormalEntityClass() {
        byte[] array = serializer.serialize(object);
        Object result = serializer.deserialize(array);
        assertNotNull(result);
        assertEquals(object, result);
    }

    @Test
    void testMoreComplexEntityClass() {
        byte[] array = serializer.serialize(moreComplexObject);
        Object result = serializer.deserialize(array);
        assertNotNull(result);
        assertEquals(moreComplexObject, result);
    }
}
