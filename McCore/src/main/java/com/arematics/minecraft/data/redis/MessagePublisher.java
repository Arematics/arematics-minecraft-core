package com.arematics.minecraft.data.redis;

public interface MessagePublisher {
    void publish(String message);
}
