package com.commerce.common.config;


import com.commerce.common.properties.CacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * RedisStandAloneConfig.java
 *
 * @Profile("local") : profile 이 local 일때 사용되는 Config
 * 					   local 에선 StandAlone Type 의 Redis 를 사용하므로
 */
@Profile("local")
@EnableRedisHttpSession
@EnableTransactionManagement
@EnableCaching
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisStandAloneConfig {


	private final CacheProperties cacheProperties;

	@Value("${spring.redis.cache.host}")
	private  String redisHost;

	@Value("${spring.redis.cache.port}")
	private  int redisPort;

	@Bean(name = "redisCacheConnectionFactory")
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost,redisPort));
	}

	/*
	 * Jackson2는 Java8의 LocalDate의 타입을 알지못해서 적절하게 직렬화해주지 않으므로 역직렬화 시 에러가 발생한다.
	 * 따라서 적절한 ObjectMapper를 Serializer에 전달하여 직렬화 및 역직렬화를 정상화함.
	 */
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

	/*
	 * properties에서 가져온 캐시명과 ttl 값으로 RedisCacheConfiguration을 만들고 Map에 넣어 반환한다.
	 */
	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

		for (Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
			cacheConfigurations
				.put(cacheNameAndTimeout.getKey(),
					redisCacheDefaultConfiguration().entryTtl(Duration.ofSeconds(cacheNameAndTimeout.getValue())));
		}
		return cacheConfigurations;
	}

	@Bean
	public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheDefaultConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
		return redisCacheManager;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<Object,Object> createRedisTemplate(){
		RedisTemplate<Object,Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		//redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		//redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		redisTemplate.setEnableTransactionSupport(true); // transaction setting
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	// spring session
	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean
	public ZSetOperations<Object, Object> zSetOperations() {
		return createRedisTemplate().opsForZSet();
	}

}
