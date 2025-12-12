# Architecture 문서

## 목차
- [시스템 아키텍처](#시스템-아키텍처)
- [계층 구조](#계층-구조)
- [패키지 구조](#패키지-구조)
- [기술 스택](#기술-스택)
- [보안 아키텍처](#보안-아키텍처)
- [데이터베이스 설계](#데이터베이스-설계)

## 시스템 아키텍처

```
┌─────────────┐
│   Client    │ (Browser, Postman, Mobile App)
└──────┬──────┘
       │ HTTP/HTTPS
       │
┌──────▼──────────────────────────────────────┐
│         Spring Boot Application             │
│  ┌────────────────────────────────────────┐ │
│  │         Controller Layer               │ │
│  │  (REST API Endpoints)                  │ │
│  └─────────────┬──────────────────────────┘ │
│                │                             │
│  ┌─────────────▼──────────────────────────┐ │
│  │          Service Layer                 │ │
│  │  (Business Logic)                      │ │
│  └─────────────┬──────────────────────────┘ │
│                │                             │
│  ┌─────────────▼──────────────────────────┐ │
│  │       Repository Layer                 │ │
│  │  (Data Access with JPA)                │ │
│  └─────────────┬──────────────────────────┘ │
└────────────────┼──────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
┌───────▼──────┐   ┌──────▼─────┐
│    MySQL     │   │   Redis    │
│  (Database)  │   │   (Cache)  │
└──────────────┘   └────────────┘
```

## 계층 구조

### 1. Controller Layer (표현 계층)
- HTTP 요청/응답 처리
- 입력 검증 (`@Valid`)
- 인증/인가 확인
- DTO 변환

**주요 컴포넌트**:
- `@RestController`: REST API 엔드포인트
- `@RequestMapping`: URL 매핑
- `ApiResult<T>`: 통일된 응답 형식

### 2. Service Layer (비즈니스 로직 계층)
- 핵심 비즈니스 로직 구현
- 트랜잭션 관리 (`@Transactional`)
- 여러 Repository 조합
- 예외 처리

**주요 컴포넌트**:
- `@Service`: 비즈니스 로직
- `@Transactional`: 트랜잭션 관리

### 3. Repository Layer (데이터 접근 계층)
- 데이터베이스 CRUD
- JPA Query Method
- Custom Query (`@Query`)

**주요 컴포넌트**:
- `JpaRepository`: Spring Data JPA
- Query Methods: `findByXXX`, `existsByXXX`

### 4. Domain Layer (도메인 모델 계층)
- Entity 클래스
- DTO (Data Transfer Object)
- Enum (상태, 역할 등)

**주요 컴포넌트**:
- `@Entity`: JPA 엔티티
- `@Builder`: 객체 생성 패턴
- DTOs: Request/Response 객체

## 패키지 구조

```
src/main/java/com/wsd/bookstoreapi/
├── domain/                    # 도메인별 패키지
│   ├── auth/                  # 인증
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dto/
│   │   └── entity/
│   ├── user/                  # 사용자
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   └── entity/
│   ├── book/                  # 도서
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── dto/
│   │   └── entity/
│   ├── order/                 # 주문
│   ├── review/                # 리뷰
│   ├── favorite/              # 찜
│   └── cart/                  # 장바구니
│
├── global/                    # 전역 설정 및 공통 기능
│   ├── api/                   # API 공통 응답
│   │   └── ApiResult.java
│   ├── config/                # 설정
│   │   ├── SecurityConfig.java
│   │   ├── CorsConfig.java
│   │   └── SwaggerConfig.java
│   ├── error/                 # 에러 처리
│   │   ├── ErrorCode.java
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── security/              # 보안
│   │   ├── jwt/
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtTokenProvider.java
│   │   ├── UserPrincipal.java
│   │   ├── SecurityUtil.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAccessDeniedHandler.java
│   ├── logging/               # 로깅
│   │   └── RequestLoggingFilter.java
│   └── rate/                  # 레이트 리미팅
│       └── RateLimitingFilter.java
│
└── BookstoreApiApplication.java

src/main/resources/
├── application.yml            # 메인 설정
├── application-local.yml      # 로컬 환경 설정
├── application-prod.yml       # 운영 환경 설정
└── db/migration/              # Flyway 마이그레이션
    ├── V1__baseline.sql
    └── V2__seed_data.sql
```

## 기술 스택

### Backend Framework
- **Spring Boot 3.4.0**
- **Spring Security**: 인증/인가
- **Spring Data JPA**: 데이터 접근
- **Spring Validation**: 입력 검증

### Database
- **MySQL 8.0**: 관계형 데이터베이스
- **Redis 7**: 세션 관리, 토큰 블랙리스트
- **Flyway**: 데이터베이스 마이그레이션

### Security
- **JWT (JSON Web Token)**: 인증
- **BCrypt**: 비밀번호 해싱
- **CORS**: Cross-Origin 리소스 공유

### Documentation
- **SpringDoc OpenAPI 3**: Swagger UI 자동 생성
- **Swagger Annotations**: API 문서화

### Build & Deploy
- **Gradle**: 빌드 도구
- **Docker**: 컨테이너화
- **Docker Compose**: 멀티 컨테이너 관리

### Testing
- **JUnit 5**: 단위 테스트
- **Spring Boot Test**: 통합 테스트
- **Mockito**: Mock 객체

## 보안 아키텍처

### 인증 흐름

```
1. 로그인 요청
   Client → POST /auth/login (email, password)

2. 인증 처리
   AuthService → 사용자 조회
   AuthService → BCrypt 비밀번호 검증

3. 토큰 발급
   JwtTokenProvider → Access Token 생성 (15분)
   JwtTokenProvider → Refresh Token 생성 (1시간)
   Redis → Refresh Token 저장

4. 토큰 반환
   Server → Client (accessToken, refreshToken)

5. API 요청
   Client → API Request (Authorization: Bearer {accessToken})

6. 토큰 검증
   JwtAuthenticationFilter → 토큰 추출
   JwtAuthenticationFilter → 토큰 유효성 검증
   JwtAuthenticationFilter → SecurityContext 설정

7. 권한 확인
   SecurityConfig → 엔드포인트별 권한 검사
   Controller → 비즈니스 로직 실행
```

### 필터 체인

```
Request
   │
   ├─► RequestLoggingFilter (모든 요청 로깅)
   │
   ├─► RateLimitingFilter (레이트 리미팅, 로그인/회원가입만)
   │
   ├─► JwtAuthenticationFilter (JWT 토큰 검증)
   │    ├─ shouldNotFilter: /health, /swagger-ui, /api/v1/auth
   │    ├─ Token 추출 및 검증
   │    ├─ 블랙리스트 체크
   │    ├─ 비활성화 사용자 체크
   │    └─ SecurityContext 설정
   │
   ├─► SecurityFilterChain (Spring Security)
   │    ├─ 권한 체크 (ROLE_USER, ROLE_ADMIN)
   │    └─ 인증 여부 확인
   │
   └─► Controller (비즈니스 로직)
```

### 보안 기능

1. **JWT 토큰 관리**
   - Access Token: 단기(15분)
   - Refresh Token: 장기(1시간)
   - 블랙리스트: 로그아웃 토큰 Redis 저장

2. **비밀번호 보안**
   - BCrypt 해싱 (strength=10)
   - 평문 비밀번호 저장 금지

3. **CORS 설정**
   - 허용 도메인: localhost:3000, localhost:8080, localhost:9090
   - 허용 메서드: GET, POST, PUT, PATCH, DELETE, OPTIONS

4. **레이트 리미팅**
   - 로그인/회원가입: 분당 30회 제한
   - IP 기반 카운팅

5. **계정 보안**
   - 비활성화 계정 API 접근 차단
   - 활성화 API만 접근 허용

## 데이터베이스 설계

### ERD 개요

```
users (1) ──┬── (N) orders
            ├── (N) reviews
            ├── (N) favorites
            └── (1) cart ── (N) cart_items

books (1) ──┬── (N) reviews
            ├── (N) favorites
            ├── (N) cart_items
            └── (N) order_items

orders (1) ── (N) order_items
```

### 주요 테이블

1. **users**: 사용자 정보
   - 인증: email(UK), password
   - 프로필: name, role, provider, status
   - 감사: created_at, updated_at

2. **books**: 도서 정보
   - 기본 정보: title, author, publisher, isbn(UK)
   - 판매 정보: price, stock_quantity
   - 메타: category, description, published_at
   - 상태: is_active

3. **orders**: 주문
   - 연관: user_id(FK)
   - 상태: status (PENDING, SHIPPED, DELIVERED, CANCELED)
   - 금액: total_amount
   - 배송: shipping_address

4. **order_items**: 주문 상세
   - 연관: order_id(FK), book_id(FK)
   - 수량/가격: quantity, unit_price, line_total

5. **reviews**: 리뷰
   - 연관: user_id(FK), book_id(FK)
   - 내용: rating, content
   - 제약: UK(user_id, book_id) - 한 사용자당 도서 1개 리뷰

6. **favorites**: 찜
   - 연관: user_id(FK), book_id(FK)
   - 제약: UK(user_id, book_id)

7. **cart**: 장바구니
   - 연관: user_id(FK, UK) - 사용자당 1개 장바구니

8. **cart_items**: 장바구니 항목
   - 연관: cart_id(FK), book_id(FK)
   - 수량: quantity

### 인덱스 전략

- **Primary Key**: 모든 테이블 `id` 컬럼
- **Foreign Key**: 모든 연관 관계에 인덱스 자동 생성
- **Unique Key**:
  - users.email
  - books.isbn
  - cart.user_id
  - reviews(user_id, book_id)
  - favorites(user_id, book_id)
- **검색 최적화**: books.title, books.author (Flyway에서 인덱스 정의)

## 의존성 관리

### 계층간 의존 방향

```
Controller → Service → Repository → Entity
     ↓          ↓
    DTO    BusinessException
```

### 의존성 주입

- **생성자 주입** 사용 (`@RequiredArgsConstructor`)
- Field Injection 지양
- 순환 참조 방지

## 예외 처리 전략

1. **BusinessException**: 비즈니스 로직 예외
   - ErrorCode와 메시지 포함
   - GlobalExceptionHandler에서 catch

2. **Validation Exception**: 입력 검증 실패
   - `@Valid` 어노테이션
   - MethodArgumentNotValidException

3. **Security Exception**: 인증/인가 실패
   - JwtAuthenticationEntryPoint
   - JwtAccessDeniedHandler

## 트랜잭션 관리

- **선언적 트랜잭션**: `@Transactional`
- **읽기 전용 최적화**: `@Transactional(readOnly = true)`
- **격리 수준**: READ_COMMITTED (기본값)
- **전파 방식**: REQUIRED (기본값)

## 성능 최적화

1. **N+1 문제 방지**
   - `@EntityGraph` 사용
   - Fetch Join 쿼리

2. **페이지네이션**
   - Spring Data Pageable
   - 대량 데이터 조회 최적화

3. **캐싱**
   - Redis: Refresh Token, 블랙리스트
   - (향후) Query Result Cache 추가 가능

4. **Connection Pool**
   - HikariCP (Spring Boot 기본)
   - 최적화된 커넥션 관리

## 배포 아키텍처

```
Docker Compose
├── bookstore-app (Spring Boot)
├── mysql (MySQL 8.0)
└── redis (Redis 7)
```

- **포트 매핑**:
  - App: 9090
  - MySQL: 3306
  - Redis: 6379
- **헬스체크**: 각 컨테이너별 헬스체크 설정
- **볼륨**: MySQL 데이터 영속성
- **네트워크**: Docker bridge 네트워크
