package com.arematics.minecraft.core.configurations.spring;

import com.arematics.minecraft.data.redis.GlobalRedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class GlobalRedisConfiguration {

    @Value("${spring.redis.host}")
    private String redisGlobalHost;

    @Value("${spring.redis.port}")
    private Integer redisGlobalPort;

    @Bean("globalCacheFactory")
    public JedisConnectionFactory globalJedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisGlobalHost, redisGlobalPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean("globalCache")
    public CacheManager globalCacheManager(@Qualifier("globalCacheFactory") JedisConnectionFactory globalCacheFactory,
                                           RedisCacheConfiguration redisCacheConfiguration) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(globalCacheFactory)
                .cacheDefaults(redisCacheConfiguration);
        return builder.build();
    }

    @Bean("globalTopic")
    ChannelTopic topic() {
        return new ChannelTopic("globalQueue");
    }

    @Bean("globalMessageListener")
    MessageListenerAdapter messageListener(GlobalRedisMessageSubscriber globalRedisMessageSubscriber) {
        return new MessageListenerAdapter(globalRedisMessageSubscriber);
    }

    @Bean("globalRedisContainer")
    RedisMessageListenerContainer redisContainer(@Qualifier("globalMessageListener") MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(globalJedisConnectionFactory());
        container.addMessageListener(messageListenerAdapter, topic());
        return container;
    }

    @Bean("globalRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("globalCacheFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean("globalStringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("globalCacheFactory") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
