package com.commerce.common.config;


import static io.lettuce.core.ReadFrom.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.commerce.common.properties.CacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Profile("prod") : profile 이 prod 일 경우에만 사용되는 Config file 이다.
 * Redis Cluster 는 Kubernetes 에 구성돼있으며, Spring Boot Project 는 {service-name}:{namespace} 를 이용하여 Redis와 연결된다.
 */

@Profile("prod")
@EnableRedisHttpSession
@EnableTransactionManagement
@EnableCaching
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisClusterConfig {

	private final CacheProperties cacheProperties;

	@Value("${spring.redis.cluster.nodes}")
	private List<String> clusterNodes;

	@Value("${spring.redis.cluster.max-redirect}")
	private int maxRedirect;

	// RedisTemplate 직렬화
	@Bean
	public RedisTemplate<String,Object> redisTemplate(){
		RedisTemplate redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		redisTemplate.setEnableTransactionSupport(true); // transaction setting
		return redisTemplate;
	}

	// Redis connection 설정
	@Bean(name = "redisCacheConnectionFactory")
	public RedisConnectionFactory connectionFactory() {

		// 클러스터 호스트 세팅
		RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration(clusterNodes);
		redisClusterConfig.setMaxRedirects(maxRedirect);

		// topology 자동 업데이트 옵션 추가
		// enablePeriodicRefresh(tolpology 정보 감시 텀) default vaule : 60s
		ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
			.enableAllAdaptiveRefreshTriggers()  // MOVED, ASK, PERSISTENT_RECONNECTS, UNCOVERED_SLOT, UNKOWN_NODE trigger시 refresh 진행
			.build();

		ClientOptions clientOptions = ClusterClientOptions.builder()
			.topologyRefreshOptions(clusterTopologyRefreshOptions)
			.build();

		// topology 옵션 및 timeout 세팅
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
			.clientOptions(clientOptions)
			.commandTimeout(Duration.ofSeconds(2000L)) // timeout Duration 값
			.readFrom(REPLICA_PREFERRED) // slave 에서 읽도록 설정
			.build();

		return new LettuceConnectionFactory(redisClusterConfig,clientConfiguration);
	}

	// Redis 에서 사용할 object mapper 설정
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

	// 직렬화, 역직렬화 설정
	private RedisCacheConfiguration redisCacheDefaultConfiguration() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));
		return redisCacheConfiguration;
	}

	// Redis {cache name} : {ttl} 설정
	private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		for (Map.Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
			cacheConfigurations
				.put(cacheNameAndTimeout.getKey(), redisCacheDefaultConfiguration().entryTtl(
					Duration.ofSeconds(cacheNameAndTimeout.getValue())));
		}
		return cacheConfigurations;
	}

	// spring session
	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	/*
		Redis cacheManager 설정
		@Cacheable : 스프링 Cache 에서 사용할 용도
	 */
	@Bean(name="redisCacheManager")
	public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheDefaultConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap())
			.build();
		return redisCacheManager;
	}

	@Bean
	public ZSetOperations<String,Object> zSetOperations(){
		return redisTemplate().opsForZSet();
	}

}