package org.samsung.api.common;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;


@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    private final Logger log = LogManager.getLogger(this.getClass());

    private @Value("${spring.data.redis.host}")
    String redisHost;

    private @Value("${spring.data.redis.port}")
    int redisPort;

    private @Value("${spring.data.redis.timeout}")
    long redisTimeOut;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {

        log.info("=================================================================");
        log.info("redis config : {} : {} : {} ", redisHost, redisPort, redisTimeOut);
        log.info("=================================================================");

        //https://jane096.github.io/project/redis-caching-part2/
        // For Memory : https://www.oss.kr/storage/app/public/festival/track2/2-1.pdf

        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigurationBuilder
                = JedisClientConfiguration.builder();
        jedisClientConfigurationBuilder.connectTimeout(Duration.ofMillis(redisTimeOut));
        jedisClientConfigurationBuilder.readTimeout(Duration.ofMillis(redisTimeOut));

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(
                redisStandaloneConfiguration, jedisClientConfigurationBuilder.build());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    RedisCacheWriter redisCacheWriter() {
        return RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory());
    }

    @Bean
    RedisCacheConfiguration defaultRedisCacheConfiguration() {

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .prefixCacheNameWith("collection::").entryTtl(Duration.ofDays(1));
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();

        cacheNamesConfigurationMap.put("ParserCache",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)));

        cacheNamesConfigurationMap.put("MrcCache",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7)).disableCachingNullValues() );
        //.disableCachingNullValues() 사용하는 곳에서 unless ="#result==null" 필수

        return new RedisCacheManager(redisCacheWriter(), defaultRedisCacheConfiguration(),
                cacheNamesConfigurationMap);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    @Slf4j
    public static class RedisCacheErrorHandler implements CacheErrorHandler {

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.error("[Cache] Unable to get from " + cache.getName() + " : " + exception.getMessage());
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.error("[Cache] Unable to put into " + cache.getName() + " : " + exception.getMessage());
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.error("[Cache] Unable to evict from " + cache.getName() + " : " + exception.getMessage());
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.error("[Cache] Unable to clean " + cache.getName() + " : " + exception.getMessage());
        }
    }
}