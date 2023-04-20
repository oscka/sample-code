# Caffeine Cache (Local Cache) 사용법

## 1. Caffeine Cache 란

Caffeine Cache 는 Spring 진영에서 많이 쓰이는 Local Cache 라이브러리이다.

### 1.1. Caffeine Cache 와 타 Cache 비교

Caffeine 은 주로 쓰기 성능 측면에서 Ehcache보다 높다.

Ehcache 는 더 많은 Cache 기능을 사용할 수 있다.


## 2. Spring Boot 에 Caffeine Cache 적용

### 2.1. build.gradle (gradle 기준)

implementation 'com.github.ben-manes.caffeine:caffeine'

dependency 추가

## 2.2. Caffeine Config

### 2.2.1. yml configuration

````yaml
spring:
  cache:
  caffeine:
    spec: maximumSize=500,expireAfterWrite=5s
    type: caffeine
    cache-names:
    - product
    - category
````
yaml 로 설정할 경우 작성이 간편하지만 ttl 과 size 와 같은 cache 설정을 각각 지정할수 없으므로

개별 설정이 필요하다면 java 로 configuration file 을 작성해야한다.

#### 2.2.2. java configuration

**2.2.2.1. CacheType.java**

```java
@Getter
public enum CacheType {

	CATEGORY("category",24 * 60 * 60, 500), // 1day
	PRODUCT("product");

	private final String name;
	private final int expireAfterWrite;
	private final int maximumSize;

	CacheType(String name){
		this.name = name;
		this.expireAfterWrite = ConstConfig.DEFAULT_TTL_SEC;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	CacheType(String name,int expireAfterWrite){
		this.name = name;
		this.expireAfterWrite = expireAfterWrite;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	CacheType(String name,int expireAfterWrite,int maximumSize){
		this.name = name;
		this.expireAfterWrite = expireAfterWrite;
		this.maximumSize = maximumSize;
	}

	public static class ConstConfig {
		static final int DEFAULT_TTL_SEC = 3000; //50 * 60 (50분)
		static final int DEFAULT_MAX_SIZE = 10000;
	}
}
```
cache 의 이름,  TTL (만료 시간) , max size (cache 최대 보관 갯수) 를 선언한 enum 을 생성한다.

그후 cache name 별 ttl , max size 를 설정한다.

CATEGORY cache 의 경우 TTL : 1day , maxSize: 500 으로 적용했으며, 그 이외의 cache 는 default 로 선언한 값이 적용했다.



**2.2.2.2. CaffeineConfig.java**
```java
@Slf4j
@EnableCaching
@Configuration
public class CaffeineConfig  {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        // Enum CacheType.class  값을 가져온다.
		List<CaffeineCache> caches = Arrays.stream(CacheType.values())
			.map(cache -> new CaffeineCache(
				cache.getName(),
				Caffeine.newBuilder()
					// expireAfterWrite: 항목이 생성된 후 또는 해당 값을 가장 최근에 바뀐후 특정 기간이 지나면 각 항목이 캐시에서 자동으로 제거되도록 설정
					.expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
					// maximumSize: 캐시에 포함할 수 있는 최대 엔트리수를 지정
					.maximumSize(cache.getMaximumSize())
					// cache 가 evicted 될때 로깅 남기는 용도
					.removalListener((key, value, cause) -> {
						if (cause.wasEvicted()) {
							log.debug("[caffeine cache] [key]: {}, [value]: {}", key, value);
						}
					})
					.recordStats()
					.build()
			))
			.collect(Collectors.toList());

        // spring cache annotation 을 사용하기 위한 용도
		cacheManager.setCaches(caches);
		return cacheManager;
	}

}
```
CaffeineConfig 에 위에서 만든 CacheType.java을 import 한후, Cache name 별 설정을 적용한다.

Config Desc

- expireAfterWrite : 캐시가 생성된 후 또는 해당 값을 가장 최근에 바뀐후 특정 기간이 지나면 각 항목이 캐시에서 자동으로 제거되도록 설정

- expireAfterWrite : write 후 특정시간이 지날경우

- expireAfterAccess : access (read) 이후 특정시간이 지날경우

- maximumSize : 캐시에 포함 할수 있는 최대 엔트리 수