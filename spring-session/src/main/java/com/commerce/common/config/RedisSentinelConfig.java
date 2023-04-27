package com.commerce.common.config;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Redis Sentinel 구성일 경우 사용하는 Config
 * 현재 Prod 가 Cluster 로 구성돼있으므로 해당 Class 는 사용하지 않는다.
 */
@Profile("sentinel")
@EnableCaching
@Configuration
public class RedisSentinelConfig {


	@Value("${spring.redis.port}")
	private int redisPort;

	@Value("${spring.redis.password}")
	private String redisPassword;

	@Value("${spring.redis.sentinel.master}")
	private String sentinelMasterName;

	@Value("${spring.redis.sentinel.nodes}")
	private String sentinelNodes;
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Bean
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	public RedisConnectionFactory redisConnectionFactory() {
		RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
		sentinelConfig.setPassword(RedisPassword.of(redisPassword));
		sentinelConfig.setSentinelPassword(RedisPassword.of(redisPassword));

		sentinelConfig.master(sentinelMasterName);
		for(String s : sentinelNodes.split(",")) {
			sentinelConfig.sentinel(s, redisPort);
		}
		return new LettuceConnectionFactory(sentinelConfig);
	}

	private ObjectMapper objectMapper(){
		// ref https://www.baeldung.com/jackson-inheritance
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
			.builder()
			.allowIfSubType(Object.class)
			.build();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

		//javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		//javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	@Bean(name = "redisCustomTemplate")
	public RedisTemplate<String, Object> redisTemplate() {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));

		return redisTemplate;
	}



	/**
	 * Redis Cache Manager
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){

		RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofSeconds(1800))
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(defaultCacheConfiguration)
			.build();
	}

	@Bean
	public ZSetOperations<String, Object> zSetOperations() {
		return redisTemplate().opsForZSet();
	}

	@Bean
	public ValueOperations<String, Object> valueOperations() {
		return redisTemplate().opsForValue();
	}

}
