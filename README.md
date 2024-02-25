# Spring Webflux 학습

### Udemy 강의

https://ssginc.udemy.com/course/build-reactive-restful-apis-using-spring-boot-webflux

### WebFlux 란? ###
- Non-Blocking 을 지향하는 API 통신
- restTemplate 대체하여 webClient 사용

### 공식 문서 ###
https://docs.spring.io/spring-framework/reference/web/webflux.html


### 학습목표 ###
- webflux 사용방법, Junit5 로 테스트 코드 작성 방법
- wireMock 로 MSA  통합 테스트


stream 을 사용하면 세션 물려있는것처럼 API 데이터를 받아 올수 있음
* 예시 실시간으로 변경되는 데이터 센서값, 주식 현재가 등
```
@GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
```
