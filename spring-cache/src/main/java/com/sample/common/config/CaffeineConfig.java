package com.sample.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sample.common.constant.CacheType;
import com.sample.common.util.CacheKeyGenerator;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
public class CaffeineConfig  {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		List<CaffeineCache> caches = Arrays.stream(CacheType.values())
			.map(cache -> new CaffeineCache(
				cache.getName(),
				Caffeine.newBuilder()
					// expireAfterWrite: 항목이 생성된 후 또는 해당 값을 가장 최근에 바뀐후 특정 기간이 지나면 각 항목이 캐시에서 자동으로 제거되도록 설정
					.expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
					// maximumSize: 캐시에 포함할 수 있는 최대 엔트리수를 지정
					.maximumSize(cache.getMaximumSize())
					.removalListener((key, value, cause) -> {
						if (cause.wasEvicted()) {
							log.debug("[caffeine cache] [key]: {}, [value]: {}", key, value);
						}
					})
					.recordStats()
					.build()
			))
			.collect(Collectors.toList());

		cacheManager.setCaches(caches);
		return cacheManager;
	}

	/**
	 * custom key 를 만들 때 사용
	 * [ex] @Cacheable(value = "category", keyGenerator = "cacheKeyGenerator")
	 */
	@Bean("cacheKeyGenerator")
	public KeyGenerator keyGenerator() {
		return new CacheKeyGenerator();
	}

}