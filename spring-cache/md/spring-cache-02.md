## 1. Spring 의 Cache 추상화

Spring 은 AOP 방식을 이용하여 편리하게 메서드에 Cache 서비스를 적용하는 기능을 제공하고 있다.

이를 통해 캐시 관련 로직을 핵심 비지니스 로직으로 부터 분리할 뿐만 아니라, 손쉽게 캐시 기능을 적용할 수 있다.

해당 기능은 Spring 의 @Transactional 어노테이션 동작원리와 비슷하며, 프록시는 Spring AOP 를 이용하여 동작하지만 Mode 설정을 통해 AspectJ 를 이용한 방법으로 설정해 줄 수도 있다.

또한 Spring 은 캐시 구현 기술에 종속되지 않도록 추상화된 서비스를 제공하고 있다.

그렇기 때문에 적용할 캐시 기술을 변경하더라도 비즈니스 로직의 코드에 영향을 주지않는다.

( 캐시 기술 :  Redis , Caffein Cache , EhCache … )

## 2. Spring Cache 사용법 및 어노테이션 종류

### 2.1 build.gradle (gradle 기준)

build.gradle 에 아래 dependency 를 추가한다.

```groovy
// cache
implementation 'org.springframework.boot:spring-boot-starter-cache'
```
위 dependency 만 추가하면 기본 CacheManager 인 ConcurrentMapCacheManager 와 SimpleCacheManager 만 사용할수 있다.

만약 Redis , Caffeine, EhCache 와 같은 특정 Cache 를 사용하고 싶다면 아래와 같이 추가적인 dependency 가 필요하다.
```groovy
// local cache (caffeine)
implementation 'com.github.ben-manes.caffeine:caffeine'

// local cache (ehcache)
// https://mvnrepository.com/artifact/net.sf.ehcache/ehcache
implementation 'net.sf.ehcache:ehcache:2.10.6'

// global cache (redis)
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
````

### 2.2. @EnableCaching 추가

````java
@EnableCaching
@Configuration
public class CaCheConfig  {
// 생략
}
````
**@EnableCaching** : @Cacheable, @CacheEvict 등의 캐시 어노테이션 활성화를 위한 어노테이션이다.

### 2.3. Cache Manager Bean 추가

캐시를 관리해줄 CacheManager 를 빈으로 등록해줘야한다.

Spring 은 CacheManager interface 를 구현하여 다양한 종류의 CacheManager 를 사용할수 있도록 했다.(org.springframework.cache.CacheManager)

Spring 에서 제공하는 Cache Manager 는 아래와 같다.

- ConcurrentMapCacheManager : Java의ConcurrentHashMap을 사용해 구현한 캐시를 사용하는 캐시 매니저
  캐시 저장소는 메모리 기반이며 애플리케이션 라이프사이클에 얽혀있기 때문에, 개발이나 작은규모에서 적합하다.

- SimpleCacheManager: 기본적으로 제공하는 캐시가 없어 사용할 캐시를 직접 등록하여 사용하기 위한 캐시매니저
setCaches 를 통해 구현체를 넘겨서 등록할 수 있다.

- EhCacheCacheManager: 자바에서 유명한 캐시 프레임워크 중 하나인 EhCache를 지원하는 캐시 매니저

- CompositeCacheManager: 2개 이상의 캐시 매니저를 사용하도록 지원해주는 혼합 캐시 매니저
여러 캐시 저장소를 동시에 사용해야 할 때 쓴다. (2차 캐싱) ex) Caffein Cache + Redis Cache

- CaffeineCacheManager: Java 8로 Guava 캐시를 재작성한 Caffeine 캐시를 사용하는 캐시 매니저

- JCacheCacheManager: JSR-107 기반의 캐시를 사용하는 캐시 매니저

- RedisCacheManager: Redis  캐시매니저


### 2.4. Spring Cache 어노테이션 (@Cacheable , @CacheEvict …)

#### 2.4.1. @Cacheable

```` java
@Transactional(readOnly = true)
@Cacheable(value = "product", key="#id")
public ProductDto.Response findById(Long id){

	return productRepository.findById(id).orElseThrow(()
		-> new RuntimeException("존재하지 않는 상품입니다.")).toProductDto();
}
````
@Cacheable : 캐시에 저장/조회 할 때 사용한다.

위 메서드는 개별 Product Info 를 반환하는 메서드 이다.

findById() 메서드 처음 호출 (값이 없음)

"product"캐시에 id로 생성된 Key 가 있는지 확인한다.

"product"캐시에 값이 없으므로 해당 로직을 실행하여 값을 반환한다.

반환된 값을 캐시에 "product" 의 value 로 저장한다.

findById() 메서드 두번째 호출 (값이 있음)

"product"캐시에 id로 생성된 Key 가 있는지 확인한다.

"product"캐시에 값이 있으므로 해당 로직을 실행 하지않고 캐시에서 조회한 값을 반환한다.

findById() 메서드 세번째 호출 ("product" 값이 바뀜)

"product" 캐시에 해당하는 값이 있는지 확인한다. (값이 없음)

"product"캐시에 값이 없으므로 해당 로직을 실행하여 값을 반환한다.

반환된 값을 캐시에 "product" 의 value 로 저장한다.


#### 2.4.2. @CachePut
````java
@CachePut(value = "product", key="#id")
````
@CachePut : 캐시에 저장 할 때,  해당 메서드를 실행해야할 때 사용한다.

메서드 실행 결과를 캐시에 저장하며, 이미 저장된 캐시의 내용을 사용하지않는다.

@CachePut 과 @Cacheable 의 차이점

@Cacheable은 Cache 에 해당 값이 있을경우 해당 메서드를 실행하지 않음

@CachePut 은 Cache 에 있더라도 항상 해당 메서드를 실행한다.

#### 2.4.3. @CacheEvict
````java
@Transactional
@CacheEvict(value = "product",key="#id")
public void delete(Long id){

	Product product = productRepository.findById(id).orElseThrow(()
	          ->new RuntimeException("존재하지 않는 상품입니다."));

	productRepository.delete(product);
}
````
캐시를 제거하는 방법은 아래의 2가지가 있다.

일정한 주기로 캐시를 제거 (cache ttl)

값이 변할 때 캐시를 제거

값이 달라질 때 캐시를 제거하지 않으면 잘못된 결과가 반환될 것이다.

@CacheEvict: 캐시 데이터를 삭제할때 사용한다.

allEntires = true 로 설정하면 product 이름으로 저장된 Cache 를 모두 삭제하는 것이다.

그러나 이는 Cache 성능 저하를 일으키므로, 운영환경에서는 지양해야한다.



#### 2.4.5. @Caching
````java
@Caching(evict = { @CacheEvict("primary"), @CacheEvict(value = "secondary", key = "#p0") })
````
한 메서드에서 @Cacheable @CacheEvict @CachePut 을 여러개 지정해야하는 경우

@CachePut 과 @Cacheable 어노테이션을 같이 사용하는것은 권장하지 않는다.

두 어노테이션이 다른 동작을 하기 때문에 실행순서에 따라 다른 결과가 나올수 있다.

#### 2.4.6. @CacheConfig

Class 에 Cache 설정 선언 ( @CacheConfig 어노테이션 이용 )
````java
@CacheConfig(cacheManager = "redisCacheManager")
@RequiredArgsConstructor
@Service
public class ProductService {

// 생략 ...
````
해당 클래스에서 동일한 cache 설정을 사용한다면, 클래스 단위에  @CacheConfig 어노테이션을 이용하여 메서드마다 반복되는 코드를  줄일수 있다.

- cacheNames  : 캐시 이름
- keyGenerator: 특정 로직에 의해 cache key를 만들고자 하는 경우 사용
- cacheManager : 사용할 cacheManager 지정 (EhCacheManager , RedisCacheManager 등 )
- cacheResolver : Cache 키에 대한 결과값을 돌려주는 Resolver (Interceptor 역할)

### 2.5. Cache 모니터링

Cache 를 처음 사용해볼때, Local Cache 부터 사용할경우 Cache Key 와 Value 값 입력이 예상되지않을수 있다.

방안 1) Redis 와 같은 Global Cache 를 사용

메서드 실행마다 redis-cli 를 통해 key 와 value 가 어떻게 변하는지 눈으로 확인하는것도 방법일 수 있다.

방안 2) 캐싱 처리 모니터링

log4j2 or logback 에서 설정

````xml
<!-- Log4j2 > Spring Cache Logging 설정  -->
<logger name="org.springframework.cache" level="trace" additivity="false" >
    <AppenderRef ref="Console" />
</logger>
````
EhCache 에서 사용 :  CacheEventListener

````java
@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {
    public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
    log.info("cache event logger message. getKey: {} / getOldValue: {} / getNewValue:{}", cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
````
CacheEventLogger 클래스를 만든후 캐싱처리가 될때 CacheEventLogger 내부의 onEvent() 메서드가 호출되며 로그가 찍히는 것을 볼수 있다.


## 3. 사용시 주의점

### 3.1. Self-Invocation

(주의) @Transaction 과 동일하게, @Cacheable 이 걸려있는 클래스 내부 메서드를 호출 (Self-Invocation) 하면 AOP 가 동작하지 않는다. ( → 캐싱이 되지않는다.)

Self-Invocation
````java
// 1) 첫번째 호출
public String firstMethod(){
log.debug("firstMethod() 메서드 호출 !! ");
return cache();
}

// 2) 두번째 호출
@Cachealbe(value = "test")
pubic String cache(){
log.debug("cache() 메서드 호출 !! ");
return "spring cache"
}
````
위와 같은 구성이 있을때 firstMethod() 의 내부에서 @Cacheable 어노테이션이 붙어있는 cache()  메서드를 호출한다.

그리고 firstMethod() 메서드와  cache() 메서드가 동일한 클래스에 있다면 이러한 구성을 Self-Invocation 이라고한다.
