package com.arematics.minecraft.core;

/*@Configuration*/
public class ModeCacheManager {

    /*@Value("${spring.redis.mode.host}")
    private String redisHost;

    @Value("${spring.redis.mode.port}")
    private Integer redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

    @Bean
    public CacheManager initRedisCacheManager(RedisConnectionFactory jedisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory);
        return builder.build();
    }*/
}
