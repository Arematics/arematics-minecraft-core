package com.arematics.minecraft.core;

import lombok.SneakyThrows;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BukkitSerializer implements RedisSerializer<Object> {


    @SneakyThrows
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(bytesOut);
        outputStream.writeObject(o);
        outputStream.flush();
        outputStream.close();
        return bytesOut.toByteArray();
    }

    @SneakyThrows
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        BukkitObjectInputStream in = new BukkitObjectInputStream(new ByteArrayInputStream(bytes));
        return in.readObject();
    }
}
