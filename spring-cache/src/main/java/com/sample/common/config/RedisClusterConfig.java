package com.sample.common.config;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sample.common.properties.CacheProperties;
import com.sample.common.util.CacheKeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

/**
 * @Profile("prod") : profile 이 prod 일 경우에만 사용되는 Config file 이다.
 * Redis Cluster 는 Kubernetes 에 구성돼있으며, Spring Boot Project 는 {service-name}:{namespace} 를 이용하여 Redis와 연결된다.
 */
@Profile("prod")
@RequiredArgsConstructor
@EnableCaching
@Configuration
public class RedisClusterConfig {

	private final CacheProperties cacheProperties;


	@Value("${spring.redis.cluster.nodes}")
	private List<String> clusterNodes;

	@Value("${spring.redis.cluster.max-redirect}")
	private int maxRedirect;

	@Bean(name = "redisCacheConnectionFactory")
	public RedisConnectionFactory connectionFactory() {

		// LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
		// 	.readFrom(REPLICA_PREFERRED)
		// 	.build();
		// return new LettuceConnectionFactory(new RedisClusterConfiguration(clusterNodes),clientConfig);

		RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterNodes);
		clusterConfiguration.setMaxRedirects(maxRedirect);
		return new LettuceConnectionFactory(clusterConfiguration);
	}

	private ObjectMapper objectMapper() {

		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
			.builder()
			.allowIfSubType(Object.class)
			.build();

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModule(new JavaTimeModule());
		mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
		return mapper;
	}

	private RedisCacheConfiguration redisCacheDefaultConfiguration() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));
		return redisCacheConfiguration;
	}

	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		for (Map.Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
			cacheConfigurations
				.put(cacheNameAndTimeout.getKey(), redisCacheDefaultConfiguration().entryTtl(
					Duration.ofSeconds(cacheNameAndTimeout.getValue())));
		}
		return cacheConfigurations;
	}

	@Bean(name="redisCacheManager")
	public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheDefaultConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap())
			.build();
		return redisCacheManager;
	}

	@Bean("cacheKeyGenerator")
	public KeyGenerator keyGenerator() {
		return new CacheKeyGenerator();
	}

}