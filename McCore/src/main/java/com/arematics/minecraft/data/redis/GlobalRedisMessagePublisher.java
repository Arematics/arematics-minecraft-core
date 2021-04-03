package com.arematics.minecraft.data.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GlobalRedisMessagePublisher implements MessagePublisher{

    private final RedisTemplate<Object, Object> globalRedisTemplate;
    private final ChannelTopic globalTopic;

    @Override
    public void publish(String message) {
        globalRedisTemplate.convertAndSend(globalTopic.getTopic(), message);
    }
}
