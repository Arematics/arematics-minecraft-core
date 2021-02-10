package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.IgnoreInAppScan;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Optional;

@Configuration
@EnableCaching
@ComponentScan(value = "com.arematics.minecraft", excludeFilters = @ComponentScan.Filter(IgnoreInAppScan.class))
@EnableJpaAuditing
@ConditionalOnClass({Bukkit.class})
class SpringSpigotAutoConfiguration {

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> Optional.of("System");
    }

    @Bean(destroyMethod = "")
    Server serverBean(Plugin plugin) {
        return plugin.getServer();
    }

    @Bean(destroyMethod = "")
    Plugin pluginBean(@Value("${spigot.plugin}") String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    @Bean(destroyMethod = "")
    BukkitScheduler schedulerBean(Server server) {
        return server.getScheduler();
    }

    @Value("${spring.redis.host}")
    private String redisGlobalHost;

    @Value("${spring.redis.port}")
    private Integer redisGlobalPort;

    @Value("${spring.redis.mode.host}")
    private String redisModeHost;

    @Value("${spring.redis.mode.port}")
    private Integer redisModePort;

    @Bean(name = "jedisConnectionFactory")
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

    @Bean(name = {"defaultRedisSerializer", "springSessionDefaultRedisSerializer"})
    @Primary
    RedisSerializer<Object> defaultRedisSerializer() {
        return new BukkitSerializer();
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration);
        return builder.build();
    }

    @Bean(name = "globalCache")
    public CacheManager globalCacheManager(RedisCacheConfiguration redisCacheConfiguration) {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisGlobalHost, redisGlobalPort);
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(new JedisConnectionFactory(configuration))
                .cacheDefaults(redisCacheConfiguration);
        return builder.build();
    }

}
