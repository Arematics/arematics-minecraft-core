package com.arematics.minecraft.core.configurations.spring;

import com.arematics.minecraft.core.BukkitSerializer;
import com.arematics.minecraft.data.redis.ModeRedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class GameModeRedisConfiguration {

    @Value("${spring.redis.mode.host}")
    private String redisModeHost;

    @Value("${spring.redis.mode.port}")
    private Integer redisModePort;

    @Bean("jedisConnectionFactory")
    @Primary
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisModeHost, redisModePort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    @Primary
    public RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new BukkitSerializer()));
    }

    @Bean
    @Primary
    public CacheManager cacheManager(@Qualifier("jedisConnectionFactory") RedisConnectionFactory jedisConnectionFactory,
                                     RedisCacheConfiguration redisCacheConfiguration) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration);
        return builder.build();
    }

    @Bean("modeTopic")
    ChannelTopic topic() {
        return new ChannelTopic("modeQueue");
    }

    @Bean("modeMessageListener")
    @Primary
    MessageListenerAdapter messageListener(ModeRedisMessageSubscriber modeRedisMessageSubscriber) {
        return new MessageListenerAdapter(modeRedisMessageSubscriber);
    }

    @Bean
    @Primary
    RedisMessageListenerContainer redisContainer(@Qualifier("modeMessageListener") MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListenerAdapter, topic());
        return container;
    }

    @Bean("modeRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean("modeStringRedisTemplate")
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
