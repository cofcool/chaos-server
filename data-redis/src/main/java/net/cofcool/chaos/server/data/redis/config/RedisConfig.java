package net.cofcool.chaos.server.data.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

/**
 * Redis configuration
 *
 * Spring Boot 会创建<code>redisTemplate</code>, <code>stringRedisTemplate</code>实例
 *
 * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
 * @see org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
 *
 * @author CofCool
 */
@Configuration
public class RedisConfig {

    /// manual setting CacheManager
//    @Bean
//    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
//        /// if use customer serializer
//        /// set RedisCacheConfiguration
//        /// use Jackson to serialize the bean: GenericJackson2JsonRedisSerializer
//        return RedisCacheManager.create(jedisConnectionFactory);
//    }
//
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, JedisClientConfiguration jedisClientConfiguration) {
//        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
//    }
//
//    @Bean
//    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setHostName(redisHostName);
//        configuration.setPassword(RedisPassword.of(redisPassWd));
//        configuration.setPort(redisPort);
//
//        return configuration;
//    }


    /**
     * auto configuration
     * @return RedisConnectionConfiguration
     *
     * @see org.springframework.boot.autoconfigure.data.redis.JedisConnectionConfiguration
     */
    @Bean
    public JedisClientConfiguration jedisClientConfiguration() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinEvictableIdleTimeMillis(43200000);
        poolConfig.setSoftMinEvictableIdleTimeMillis(43200000);

        return JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .build();
    }

}
