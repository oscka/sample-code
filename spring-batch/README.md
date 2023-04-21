# Spring Batch
- spring boot 3.0.6, java 17, gradle 7.6.1
- spring batch, spring data jpa
- spring data redis, mariadb, h2

### 상품 데이터 batch
- RDB 에서 구매량이 많은 상품을 검색한후 1시간 간격으로 Best Product 를 Redis 에 Caching 한다.