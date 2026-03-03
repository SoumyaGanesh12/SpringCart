package com.ecommerce.project.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

@Configuration
public class RedisConfig {
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
										// Cache entries expire after 10 minutes
										.entryTtl(Duration.ofMinutes(10))
										// Key Serializer
										.serializeKeysWith(
												RedisSerializationContext.SerializationPair.fromSerializer(
													new StringRedisSerializer()
												)
										)
										// Value Serializer
										.serializeValuesWith(
												RedisSerializationContext.SerializationPair.fromSerializer(
														new JdkSerializationRedisSerializer()
												)
										)
										// Don't cache null values
										.disableCachingNullValues();
		
		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(config)
				.build();
	}
}
