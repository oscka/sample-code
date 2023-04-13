
**Cache** 란 자주 사용하는 데이터나 값을 미리 복사해 놓는 임시장소를 가리킨다.

### Local Cache
Local 장비 내에서만 사용되는 캐시로, Local 장비의 Resource 를 이용한다.  
Spring 진영에서 Local Cache 로 [EhCache](https://www.ehcache.org/) , [Caffeine Cache](https://github.com/ben-manes/caffeine) 가 대표적으로 쓰인다.
- 장점
    - Local 서버 내에서 작동하기 때문에 속도가 빠르다.
- 단점
    - Local 서버 에서만 작동하기 때문에 다른 서버와 데이터 공유가 어렵다.
    - 로컬 캐시는 속도가 빠른 반면 서버가 많을 수록 동기화를 하는 부하가 생긴다.
    - Local 서버의 Resource를 사용하기 때문에 Java application 의 Full GC 가 일어났을 때 애플리케이션의 성능과 안정성 큰 영향을 준다.

### Global Cache
Spring 진영에서 Global Cache 로  Redis , Memcached 가 대표적으로 쓰인다.  
여러 서버에서 Cache Server 에 접근하여 사용하는 캐시로 분산된 서버에서 데이터를 저장하고 조회할수 있다.
- 장점
    - 별도의 Cache Server 를 이용하기 때문에 서버 간의 데이터 공유가 쉽다.
    - 데이터를 분산하여 저장할 수 있다.
        - **Replication** : 두 개 이상의 DBMS 시스템을 Master / Salve 로 나눠서 동일한 데이터를 저장하는 방식
        - **Sharding** : 같은 테이블 스키마를 가진 데이터를 다수의 데이터베이스에 분산하여 저장하는 방법
- 단점
    - 네트워크를 통해 데이터를 가져오므로, Local Cache 에 비해 상대적으로 느리다.