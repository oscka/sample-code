1. Spring 의 Cache 추상화
   Spring 은 AOP 방식을 이용하여 편리하게 메서드에 Cache 서비스를 적용하는 기능을 제공하고 있다.

(프록시는 Spring AOP 를 이용하여 동작하며 mode 설정을 통해 AspectJ 를 이용한 방법으로 설정해 줄 수도 있다.)

해당 기능은 Spring 의 @Transactional 어노테이션 동작원리와 비슷하다.

이를 통해 캐시 관련 로직을 핵심 비지니스 로직으로 부터 분리할 뿐만 아니라, 손쉽게 캐시 기능을 적용할 수 있다.

또한 Spring 은 캐시 구현 기술에 종속되지 않도록 추상화된 서비스를 제공하고 있다.

그렇기 때문에 적용할 캐시 기술을 변경하더라도 비즈니스 로직의 코드에 영향을 주지않는다. ( Redis , Caffein Cache , EhCache 로 변경 가능)



2. Spring Cache 사용법 및 어노테이션 종류
   2.1 build.gradle (gradle 기준)
   build.gradle 에 아래 dependency 를 추가한다.


// cache
implementation 'org.springframework.boot:spring-boot-starter-cache'
위 dependency 만 추가하면 기본 CacheManager 인 ConcurrentMapCacheManager 와 SimpleCacheManager 만 사용할수 있다.

만약 Redis , Caffeine, EhCache 와 같은 특정 Cache 를 사용하고 싶다면 아래와 같이 추가적인 dependency 가 필요하다.


// local cache (caffeine)
implementation 'com.github.ben-manes.caffeine:caffeine'

// local cache (ehcache)
// https://mvnrepository.com/artifact/net.sf.ehcache/ehcache
implementation 'net.sf.ehcache:ehcache:2.10.6'

// global cache (redis)
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
2.2. @EnableCaching 추가

@EnableCaching
@Configuration
public class CaCheConfig  {
// 생략
}
@EnableCaching : @Cacheable, @CacheEvict 등의 캐시 어노테이션 활성화를 위한 어노테이션이다.

2.3. Cache Manager Bean 추가
캐시를 관리해줄 CacheManager 를 빈으로 등록해줘야한다.

Spring 은 CacheManager interface 를 구현하여 다양한 종류의 CacheManager 를 사용할수 있도록 했다.(org.springframework.cache.CacheManager)

Spring 에서 제공하는 Cache Manager 는 아래와 같다. 더 자세한 내용은 Spring Docs 를 참고

ConcurrentMapCacheManager: Java의ConcurrentHashMap을 사용해 구현한 캐시를 사용하는 캐시 매니저

SimpleCacheManager: 기본적으로 제공하는 캐시가 없어 사용할 캐시를 직접 등록하여 사용하기 위한 캐시매니저

EhCacheCacheManager: 자바에서 유명한 캐시 프레임워크 중 하나인 EhCache를 지원하는 캐시 매니저

CompositeCacheManager: 1개 이상의 캐시 매니저를 사용하도록 지원해주는 혼합 캐시 매니저

CaffeineCacheManager: Java 8로 Guava 캐시를 재작성한 Caffeine 캐시를 사용하는 캐시 매니저

JCacheCacheManager: JSR-107 기반의 캐시를 사용하는 캐시 매니저



2.4. Spring Cache 어노테이션 (@Cacheable , @CacheEvict …)
2.4.1. @Cacheable

	@Cacheable(value = KEY_CATEGORY ,keyGenerator = "cacheKeyGenerator" , unless="#result == null")
	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findAllCategory(){

		return categoryRepository.findAllByStatus(CategoryStatus.ACTIVE)
			.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());
	}
@Cacheable : 캐시에 저장/조회 할 때 사용한다.

위 메서드는 Category 를 반환하는 메서드 이다. 상품 Category 는 자주 변경되지 않으므로 캐시를 적용하기 적당하다.

findAllCategory() 메서드 처음 호출 (값이 없음)

KEY_CATEGORY ("category")캐시에 cacheKeyGenerator 로 생성된 Key 가 있는지 확인한다.

KEY_CATEGORY ("category")캐시에 값이 없으므로 해당 로직을 실행하여 값을 반환한다.

반환된 값을 캐시에 KEY_CATEGORY ("category") 의 value 로 저장한다.

findAllCategory() 메서드 두번째 호출 (값이 있음)

KEY_CATEGORY ("category")캐시에 cacheKeyGenerator 로 생성된 Key 가 있는지 확인한다.

KEY_CATEGORY ("category")캐시에 값이 있으므로 해당 로직을 실행 하지않고 캐시에서 조회한 값을 반환한다.

findAllCategory() 메서드 두번째 호출 (KEY_CATEGORY ("category") 값이 바뀜)

KEY_CATEGORY ("category") 캐시에 해당하는 값이 있는지 확인한다. (값이 없음)

KEY_CATEGORY ("category")캐시에 값이 없으므로 해당 로직을 실행하여 값을 반환한다.

반환된 값을 캐시에 KEY_CATEGORY ("category") 의 value 로 저장한다.

2.4.2. @CachePut
@CachePut : 캐시에 저장/조회 할 때,  해당 메서드를 실행해야할 때 사용한다.

@CachePut 과 @Cacheable 의 차이점은 Cache 에 있을경우 @Cacheable은 해당 메서드를 실행하지않고, @CachePut 은 Cache 에 있더라도 해당 메서드를 실행하는 것이다.

비즈니스 로직에 따라 선택하여 사용할 수 있다.

2.4.3. @CacheEvict
캐시를 제거하는 방법은 아래의 2가지가 있다.

일정한 주기로 캐시를 제거 (cache ttl)

값이 변할 때 캐시를 제거

값이 달라질 때 캐시를 제거하지 않으면 잘못된 결과가 반환될 것이다.


	@Transactional
	@CacheEvict(cacheNames = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")
	public void deleteCategory(Long id){

		Category category = categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("존재하지 않은 카테고리 입니다."));

		categoryRepository.delete(category);
	}
@CacheEvict: 캐시 데이터를 삭제할때 사용한다.

allEntires = true 로 설정하면 category 이름으로 저장된 Cache 를 모두 삭제하는 것이다.

그러나 이는 Cache 성능 저하를 일으키므로, 운영환경에서는 지양해야한다.

 