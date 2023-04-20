# spring-cache
spring-cache-**.md** 내용은 [Confluence](https://osc-korea.atlassian.net/wiki/home) 에 더 자세히 나와있습니다.
1. [Spring Cache](md/spring-cache-01.md)
2. [Spring 의 Cache 추상화 (Cache Abstraction)](md/spring-cache-02.md)
3. [Caffeine Cache (Local Cache) 사용법](md/spring-cache-03.md)
4. [Spring Data Redis (Global Cache) 사용법](md/spring-cache-04.md)

## Project 기술 스택
- Spring Boot 3.0.5, Java 17, Gradle 7.6.1
- Spring Data JPA, QueryDSL
- MariaDB (prod) , H2 (local,test)
- Spring Rest Docs , log4j2
- Local Cache - **Caffeine** (local-cache)
- Global Cache - **Redis**

## Project 상세
1. **Spring Rest Docs**
브라우저에서 api 문서 접근
- URL : {server-address}**/docs/api-guide.html**
  - ex) http://localhost:18080/docs/api-guide.html

2. **H2 console**

```yaml
---
# application.yml
spring:
  profiles:
    active: local
---
# application-local.yml
spring:
  #### h2 ###
  h2:
    console:
      enabled: true
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:~/sample;MODE=MYSQL
    username: sa
    password:
```
application.yml 에서 profile 을 local 로 지정하면 application-local.yml 의 내용이 활성화된다.
브라우저에서 h2-console 접속
- URL : {server-address}**/h2-console**
  - ex) http://localhost:18080/h2-console

3. api test 방법
- VSCode 
  - (httpYac - Rest Client) Extensions 설치
  - http 확장자인 파일 테스트
- IntelliJ
  - Ultimate(유료) 일경우 http 테스트 사용가능

- [상품 API 테스트](http/product.http)
- [카테고리 API 테스트](http/category.http)
   







