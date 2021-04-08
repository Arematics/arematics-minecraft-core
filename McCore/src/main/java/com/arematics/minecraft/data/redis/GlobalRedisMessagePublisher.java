package com.arematics.minecraft.data.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GlobalRedisMessagePublisher implements MessagePublisher{

    private final StringRedisTemplate globalStringRedisTemplate;
    private final ChannelTopic globalTopic;

    @Override
    public void publish(String key, String value) {
        globalStringRedisTemplate.convertAndSend(globalTopic.getTopic(), key + ":" + value);
    }
}
