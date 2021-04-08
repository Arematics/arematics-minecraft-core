package com.arematics.minecraft.data.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ModeRedisMessagePublisher implements MessagePublisher{

    private final StringRedisTemplate modeStringRedisTemplate;
    private final ChannelTopic modeTopic;

    @Override
    public void publish(String key, String value) {
        modeStringRedisTemplate.convertAndSend(modeTopic.getTopic(), key + ":" + value);
    }
}
