# wsd-bookstore
# 📚 Bookstore API
온라인 서점 REST API (과제 2 구현 완성본)

본 프로젝트는 **과제 2: 온라인 서점 API 서버 개발**의 요구사항을 기준으로 실제 서비스를 모사한 구조로 구현되었다.  
JWT 인증, Redis 기반 Refresh Token, 전역 예외 처리, 리뷰/찜/장바구니/주문 등 전체 비즈니스 플로우를 포함한다.

Integration Test 전체 통과.

---

# 1. 기술 스택

| 영역 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Build Tool | Gradle |
| DB | MySQL 8.x |
| ORM | Spring Data JPA (Hibernate) |
| Cache / Token Store | Redis |
| Security | Spring Security, JWT |
| Validation | Jakarta Validation |
| Test | JUnit5, MockMvc |
| Documentation | Swagger-OpenAPI |

---

# 2. 프로젝트 구조
```
bookstore-api/
├── src/main/java/com/wsd/bookstoreapi/
│   ├── domain/                    # 도메인별 비즈니스 로직
│   │   ├── auth/                  # 인증 (로그인, 회원가입, 토큰)
│   │   ├── user/                  # 사용자 관리
│   │   ├── book/                  # 도서 관리
│   │   ├── review/                # 리뷰 관리
│   │   ├── favorite/              # 찜 관리
│   │   ├── cart/                  # 장바구니 관리
│   │   └── order/                 # 주문 관리
│   └── global/                    # 전역 설정 및 공통 기능
│       ├── api/                   # 공통 API 응답 포맷
│       ├── config/                # 설정 (Security, Redis 등)
│       ├── entity/                # 공통 엔티티
│       ├── error/                 # 전역 예외 처리
│       ├── health/                # 헬스체크
│       ├── logging/               # 로깅 필터
│       ├── rate/                  # 레이트 리미팅
│       ├── security/              # Spring Security & JWT
│       └── util/                  # 유틸리티
├── src/main/resources/
│   ├── db/migration/              # Flyway DB 마이그레이션
│   └── application*.properties    # 환경별 설정 파일
├── docs/                          # API 문서
├── .env.example                   # 환경변수 예제
└── build.gradle
```

---

# 3. 제공 기능 요약

## 3.1 인증(Auth)
- 회원가입
- 로그인 → AccessToken + RefreshToken 발급 (Redis 저장)
- 로그아웃 → RefreshToken 삭제 + AccessToken blacklist
- 토큰 재발급 (RefreshToken 검증 후 Access/Refresh 재발급)

## 3.2 회원(User)
### 사용자 기능
- 내 정보 조회
- 내 정보 수정
- 내 계정 비활성화
- 내 계정 영구 삭제

### 관리자 기능
- 회원 목록 조회
- 회원 상세 조회
- 회원 비활성화

## 3.3 도서(Book)
- 도서 등록/수정/삭제 (관리자)
- 도서 목록 조회 (검색/카테고리 필터)
- 도서 상세 조회

## 3.4 리뷰(Review)
- 리뷰 생성 (동일 도서 한 번만 가능 → 중복 생성 시 409)
- 리뷰 수정/삭제 (본인만)
- 리뷰 목록 조회

## 3.5 찜(Favorite)
- 찜 추가 (중복 불가 → 409)
- 찜 목록 조회
- 찜 삭제

## 3.6 장바구니(Cart)
- 장바구니 조회
- 항목 추가 (이미 있는 도서는 수량 증가)
- 수량 변경
- 항목 삭제

## 3.7 주문(Order)
- 장바구니 기반 주문 생성
- 주문 목록 조회
- 주문 상세 조회
- 주문 취소 (PENDING 상태만 가능)
- 관리자용 주문 조회
- 관리자용 주문 상태 변경

---

# 4. 공통 응답 구조 (ApiResult)

모든 API는 아래 공통 포맷을 사용한다.

### 성공 응답
```json
{
  "isSuccess": true,
  "message": "성공 메시지",
  "code": null,
  "payload": { ... }
}
```
### 실패 응답

``` 
{
  "isSuccess": false,
  "message": "에러 메시지",
  "code": "ERROR_CODE",
  "payload": null
}
```
5. 전역 예외 처리 (GlobalExceptionHandler)
세부 예외 처리 흐름은 아래와 같다:

| 상황 | HTTP | ErrorCode         |
|--|---|-------------------|
|비즈니스 규칙 위반| 4xx |BusinessException |
|@Valid 실패|400|VALIDATION_FAILED|
|JSON 파싱 실패|400|INVALID_INPUT_VALUE|
|잘못된 Path/Query 타입|400|INVALID_INPUT_VALUE|
|인증 실패|401|UNAUTHORIZED|
|권한 없음|403|FORBIDDEN|
|리소스 없음|404|RESOURCE_NOT_FOUND|
|상태 충돌|409|STATE_CONFLICT|
|너무 많은 요청|	429|TOO_MANY_REQUESTS|
|미처리 예외|500|UNKNOWN_ERROR|

6. Rate Limiting
아래 3개의 경로에 대해 IP 기반 1분당 30회 요청 제한을 적용:

```
/api/v1/auth/login

/api/v1/auth/refresh

/api/v1/auth/signup
```

초과 시 429 + ErrorCode.TOO_MANY_REQUESTS 반환.

7. N+1 쿼리 제거 (성능 개선)
적용한 최적화
7.1 찜 목록 조회 Favorite → Book JOIN
```java

@EntityGraph(attributePaths = {"book"})
Page<Favorite> findByUser(User user, Pageable pageable);
7.2 장바구니 Cart → CartItem → Book JOIN
```
```java
@EntityGraph(attributePaths = {"items", "items.book"})
Optional<Cart> findWithItemsByUser(User user);
7.3 주문 Order → OrderItem → Book JOIN
```
```java
@EntityGraph(attributePaths = {"orderItems", "orderItems.book"})
Page<Order> findByUser(User user, Pageable pageable);
필요한 연관관계를 즉시 fetch 하여 N+1 문제 제거.
```

8. 인증 구조 (JWT + Redis)
### Access Token
- 짧은 유효 기간

- 요청 인증에 사용

### Refresh Token
- Redis 저장

- 강제 로그아웃 또는 재발급 시 Redis에서 제거/갱신

### 로그아웃 흐름
- Refresh Token 삭제

- Access Token 유효시간만큼 blacklist 등록

9. 테스트 전략 (모두 통과)
### IntegrationTestSupport + MockMvc 기반 전체 테스트 수행.

테스트 포함:

- AuthControllerTest
- UserMeControllerTest
- AdminUserControllerTest
- BookControllerTest
- ReviewControllerTest
- FavoriteControllerTest
- CartControllerTest
- OrderControllerTest
- AdminOrderControllerTest

모든 시나리오(정상/실패/권한/예외)를 포함한 End-to-End 수준의 테스트.

10. 환경 변수 (.env)
```ini
JWT_SECRET=your_secret_key

JWT_AT_EXPIRE=900000
JWT_RT_EXPIRE=604800000

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bookstore
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=1234

REDIS_HOST=localhost
REDIS_PORT=6379
```
11. 실행 방법
1) MySQL 실행 후 DB 생성
```pgsql
CREATE DATABASE bookstore;
```
2) Redis 실행
```pgsql
redis-server
```
3) 애플리케이션 실행
```bash

./gradlew bootRun
```