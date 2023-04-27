## Project 기술 스택
- Spring Boot 3.0.5, Java 17, Gradle 7.6.1
- Spring Data JPA , QueryDSL
- MariaDB (prod) , H2 (local,test)
- Spring Rest Docs , log4j2
- Global Cache - **Redis**
- Spring Session - Spring Redis 

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


- [User API 테스트](http/users.http)

4. docker-compose
- [Local 에서 Redis docker-compose 로 실행](docker/docker-compose.yml)
```shell
# 실행
docker-compose up -d
# 중지
docker-compose down -v
# 로그
docker-compose logs -f 
```