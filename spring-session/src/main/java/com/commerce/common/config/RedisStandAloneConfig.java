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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Profile("local")
@EnableRedisHttpSession
@RequiredArgsConstructor
@EnableCaching
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
	 * Jackson2는 Java8의 LocalDate의 타입을 알지못해서적절하게 직렬화해주지 않는다.
	 * 때문에 역직렬화 시 에러가 발생한다.
	 * 따라서 적절한 ObjectMapper를 Serializer에 전달하여 직렬화 및 역직렬화를 정상화 시켰다.
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

	/*
	 * 기본적으로 스프링에서 지원하는 캐시 기능의 캐시 저장소는 JDK 의 ConcurrentHashMap 이며,
	 * 그 외 캐시 저장소를 사용하기 위해서는 CacheManager Bean 으로 등록하여 사용할 수 있다.
	 * 보통 스프링에서 간단하게 사용할 수 있는 Java 기반의 로컬 캐시인 Ehcache 를 많이 사용하지만,
	 * 본 어플리케이션은 지속적인 트래픽 증가로 인해 어플리케이션 서버를 점점 확장(스케일 아웃)한다는
	 * 가정하에 개발하기 때문에 Redis 를 이용한 글로벌 캐시 적용을 선택했다.
	 *
	 * 글로벌 캐시 전략은 별도의 캐시 서버를 이용하기 때문에 로컬 캐시 전략보다 캐시 조회는 느리지만
	 * 캐시에 저장된 데이터가 변경되는 경우 서버마다 변경 사항을 전달하는 작업이 필요 없기 때문에
	 * 서비스 확장으로 WAS 인스턴스가 늘어나고 Cache 데이터가 커질 수록 효과적이다.
	 *
	 * properties에서 가져온 캐시명과 ttl 값으로 만든 RedisCacheConfiguration Map을
	 * withInitialCacheConfigurations에 설정하여서 캐시 별로 만료기간을 다르게 설정하였다.
	 */
	@Bean
	public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheDefaultConfiguration())
			.withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
		return redisCacheManager;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean(name = "stringRedisTemplate")
	public StringRedisTemplate stringRedisTemplate(){
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
		stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
		stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
		stringRedisTemplate.afterPropertiesSet();
		return stringRedisTemplate;
	}


	@Bean(name = "redisTemplate")
	public RedisTemplate<String,Object> redisTemplate(){
		RedisTemplate redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());


		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
		redisTemplate.setEnableTransactionSupport(true); // transaction setting


		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());


		// transaction
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	@Bean
	public ZSetOperations<String, Object> zSetOperations() {
		return redisTemplate().opsForZSet();
	}


}
