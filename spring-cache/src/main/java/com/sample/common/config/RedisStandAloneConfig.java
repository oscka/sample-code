package com.sample.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sample.common.properties.CacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

/**
 * @Profile("local") : profile 이 local 일 경우에만 사용되는 Config File 이다.
 * local PC 환경에서는 docker-compose.yml 을 이용하여 StandAlone Redis 를 실행한후, Spring Boot Project 와 연결한다.
 */
@Profile("local")
@RequiredArgsConstructor
@EnableCaching
@Configuration
public class RedisStandAloneConfig {


	private final CacheProperties cacheProperties;

	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.port}")
	private int redisPort;

	@Bean(name = "redisCacheConnectionFactory")
	public LettuceConnectionFactory redisConnectionFactory() {

		return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost,redisPort));
	}

	/*
	 * Jackson2는 Java8의 LocalDate의 타입을 알지못해서적절하게 직렬화해주지 않는다.
	 * 때문에 역직렬화 시 에러가 발생한다.
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
			.serializeKeysWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));
		return redisCacheConfiguration;
	}

	/*
	 * properties에서 가져온 캐시명과 ttl 값으로 RedisCacheConfiguration을 만들고 Map에 넣어 반환한다.
	 */
	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		for (Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
			cacheConfigurations
				.put(cacheNameAndTimeout.getKey(), redisCacheDefaultConfiguration().entryTtl(
					Duration.ofSeconds(cacheNameAndTimeout.getValue())));
		}
		return cacheConfigurations;
	}

	/**
	 * properties에서 가져온 캐시명과 ttl 값으로 만든 RedisCacheConfiguration Map을
	 * withInitialCacheConfigurations 에 설정 후, 캐시 별로 만료기간을 다르게 설정함.
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheDefaultConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
		return redisCacheManager;
	}

}
