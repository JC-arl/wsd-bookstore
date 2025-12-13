# 📚 Bookstore API

온라인 서점 시스템을 위한 RESTful API 서버입니다.

## 목차
- [프로젝트 개요](#프로젝트-개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [실행 방법](#실행-방법)
- [환경변수 설명](#환경변수-설명)
- [배포 주소](#배포-주소)
- [인증 플로우](#인증-플로우)
- [역할/권한표](#역할권한표)
- [예제 계정](#예제-계정)
- [엔드포인트 목록](#엔드포인트-목록)
- [API 응답 형식](#api-응답-형식)
- [에러 코드](#에러-코드)
- [성능/보안 고려사항](#성능보안-고려사항)
- [테스트](#테스트)
- [문서](#문서)

## 프로젝트 개요

본 프로젝트는 온라인 서점의 핵심 기능을 구현한 REST API 서버입니다.

### 문제 정의
- 도서 검색 및 상세 정보 제공
- 사용자 리뷰 및 평점 시스템
- 찜하기, 장바구니, 주문 관리
- 관리자의 도서/사용자/주문 관리

### 해결 방안
- Spring Boot 기반 RESTful API
- JWT 인증 및 역할 기반 권한 관리
- Redis를 활용한 토큰 관리
- Flyway를 통한 데이터베이스 버전 관리
- Docker Compose를 통한 손쉬운 배포

## 주요 기능

### 사용자 기능
- ✅ **회원가입/로그인**: JWT 기반 인증
- ✅ **도서 검색**: 제목, 저자, 카테고리로 검색 및 정렬
- ✅ **리뷰 시스템**: 도서별 리뷰 작성/수정/삭제
- ✅ **찜하기**: 관심 도서 저장
- ✅ **장바구니**: 구매 전 임시 저장
- ✅ **주문 관리**: 장바구니 기반 주문 생성 및 취소

### 관리자 기능
- ✅ **도서 관리**: 등록/수정/삭제/활성화
- ✅ **사용자 관리**: 목록 조회 및 계정 상태 관리
- ✅ **주문 관리**: 전체 주문 조회 및 상태 변경
- ✅ **리뷰 관리**: 부적절한 리뷰 삭제

## 기술 스택

| 영역 | 기술 | 버전 |
|------|------|------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.4.0 |
| Build Tool | Gradle | 8.x |
| Database | MySQL | 8.0 |
| Cache | Redis | 7.0 |
| ORM | Spring Data JPA | (Hibernate) |
| Security | Spring Security | + JWT |
| Migration | Flyway | Latest |
| Documentation | SpringDoc OpenAPI | 2.6.0 |
| Container | Docker | + Docker Compose |
| Testing | JUnit 5 | + MockMvc |

## 실행 방법

### Prerequisites
- Docker & Docker Compose 설치
- (또는) Java 21, MySQL 8.0, Redis 7.0

### 1. Docker Compose 사용 (권장)

```bash
# 1. 리포지토리 클론
git clone https://github.com/your-username/bookstore-api.git
cd bookstore-api

# 2. 환경변수 설정
cp .env.example .env
# .env 파일 편집하여 실제 값 입력

# 3. Docker Compose로 실행
docker-compose up -d

# 4. 헬스체크
curl http://localhost:8080/health
```

### 2. 로컬 실행

```bash
# 1. MySQL 실행 및 데이터베이스 생성
mysql -u root -p
CREATE DATABASE bookstore;

# 2. Redis 실행
redis-server

# 3. 환경변수 설정
cp .env.example .env
# .env 파일 편집

# 4. 애플리케이션 빌드 및 실행
./gradlew clean build
./gradlew bootRun

# 또는
java -jar build/libs/bookstore-api-0.0.1-SNAPSHOT.jar
```

### 3. Docker Compose 종료

```bash
# 컨테이너 중지
docker-compose down

# 데이터베이스 볼륨까지 삭제 (초기화)
docker-compose down -v
```

## 환경변수 설명

`.env.example` 파일을 복사하여 `.env` 파일을 생성하고 아래 값을 설정하세요.

| 변수명 | 설명 | 예시 값 |
|--------|------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `local` / `prod` |
| `SERVER_PORT` | 애플리케이션 포트 | `8080` |
| `DB_URL` | MySQL 접속 URL | `jdbc:mysql://mysql:3306/bookstore` |
| `DB_USERNAME` | DB 사용자명 | `bookstore_user` |
| `DB_PASSWORD` | DB 비밀번호 | `your_password` |
| `MYSQL_ROOT_PASSWORD` | MySQL root 비밀번호 | `root_password` |
| `MYSQL_DATABASE` | DB 이름 | `bookstore` |
| `REDIS_HOST` | Redis 호스트 | `redis` (Docker) / `localhost` |
| `REDIS_PORT` | Redis 포트 | `6379` |
| `JWT_SECRET` | JWT 서명 키 (Base64, 256bit 이상) | `openssl rand -base64 64` |
| `JWT_AT_EXPIRE` | Access Token 만료 시간 (ms) | `900000` (15분) |
| `JWT_RT_EXPIRE` | Refresh Token 만료 시간 (ms) | `3600000` (1시간) |

## 배포 주소

### Local
- **Base URL**: `http://localhost:8080/api/v1`
- **Swagger URL**: `http://localhost:8080/swagger-ui/index.html`
- **Health URL**: `http://localhost:8080/health`

### Production (JCloud - Docker Deployed)
- **Base URL**: `http://113.198.66.68:10088/api/v1`
- **Swagger URL**: `http://113.198.66.68:10088/swagger-ui/index.html`
- **Health URL**: `http://113.198.66.68:10088/health`

## 인증 플로우

### 1. 회원가입
```
POST /api/v1/auth/signup
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

### 2. 로그인
```
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

응답:
{
  "isSuccess": true,
  "payload": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer"
  }
}
```

### 3. API 호출
```
GET /api/v1/users/me
Headers:
  Authorization: Bearer {accessToken}
```

### 4. 토큰 갱신
```
POST /api/v1/auth/refresh
{
  "refreshToken": "{refreshToken}"
}
```

### 5. 로그아웃
```
POST /api/v1/auth/logout
Headers:
  Authorization: Bearer {accessToken}
```

## 역할/권한표

| API | Method | Path | USER | ADMIN | 비고 |
|-----|--------|------|:----:|:-----:|------|
| 회원가입 | POST | /auth/signup | ✅ | ✅ | 인증 불필요 |
| 로그인 | POST | /auth/login | ✅ | ✅ | 인증 불필요 |
| 로그아웃 | POST | /auth/logout | ✅ | ✅ | |
| 토큰 갱신 | POST | /auth/refresh | ✅ | ✅ | |
| 내 정보 조회 | GET | /users/me | ✅ | ✅ | |
| 내 정보 수정 | PATCH | /users/me | ✅ | ✅ | |
| 계정 비활성화 | PATCH | /users/me/deactivate | ✅ | ✅ | |
| 계정 활성화 | PATCH | /users/me/activate | ✅ | ✅ | |
| 계정 삭제 | DELETE | /users/me | ✅ | ✅ | |
| **사용자 관리** | | | | | |
| 사용자 목록 | GET | /admin/users | ❌ | ✅ | 관리자 전용 |
| 사용자 상세 | GET | /admin/users/{id} | ❌ | ✅ | 관리자 전용 |
| 사용자 비활성화 | PATCH | /admin/users/{id}/deactivate | ❌ | ✅ | 관리자 전용 |
| 사용자 활성화 | PATCH | /admin/users/{id}/activate | ❌ | ✅ | 관리자 전용 |
| **도서** | | | | | |
| 도서 목록 | GET | /books | ✅ | ✅ | 인증 불필요 |
| 도서 상세 | GET | /books/{id} | ✅ | ✅ | 인증 불필요 |
| 도서 등록 | POST | /admin/books | ❌ | ✅ | 관리자 전용 |
| 도서 수정 | PATCH | /admin/books/{id} | ❌ | ✅ | 관리자 전용 |
| 도서 활성화 | PATCH | /admin/books/{id}/activate | ❌ | ✅ | 관리자 전용 |
| 도서 삭제 | DELETE | /admin/books/{id} | ❌ | ✅ | 관리자 전용 |
| **리뷰** | | | | | |
| 리뷰 작성 | POST | /books/{bookId}/reviews | ✅ | ✅ | |
| 리뷰 목록 | GET | /books/{bookId}/reviews | ✅ | ✅ | 인증 불필요 |
| 내 리뷰 목록 | GET | /reviews/me | ✅ | ✅ | |
| 내 리뷰 수정 | PATCH | /books/{bookId}/reviews/me | ✅ | ✅ | 본인만 |
| 내 리뷰 삭제 | DELETE | /books/{bookId}/reviews/me | ✅ | ✅ | 본인만 |
| 전체 리뷰 조회 | GET | /admin/reviews | ❌ | ✅ | 관리자 전용 |
| 리뷰 삭제 | DELETE | /admin/reviews/{id} | ❌ | ✅ | 관리자 전용 |
| **찜** | | | | | |
| 찜 목록 | GET | /favorites | ✅ | ✅ | |
| 찜 추가 | POST | /favorites/{bookId} | ✅ | ✅ | |
| 찜 삭제 | DELETE | /favorites/{bookId} | ✅ | ✅ | |
| **장바구니** | | | | | |
| 장바구니 조회 | GET | /cart | ✅ | ✅ | |
| 항목 추가 | POST | /cart/items | ✅ | ✅ | |
| 수량 변경 | PATCH | /cart/items/book/{bookId} | ✅ | ✅ | |
| 항목 삭제 | DELETE | /cart/items/book/{bookId} | ✅ | ✅ | |
| 장바구니 비우기 | DELETE | /cart | ✅ | ✅ | |
| **주문** | | | | | |
| 주문 생성 | POST | /orders | ✅ | ✅ | |
| 내 주문 목록 | GET | /orders | ✅ | ✅ | |
| 내 주문 상세 | GET | /orders/{id} | ✅ | ✅ | 본인만 |
| 주문 취소 | PATCH | /orders/{id}/cancel | ✅ | ✅ | 본인만 |
| 전체 주문 조회 | GET | /admin/orders | ❌ | ✅ | 관리자 전용 |
| 주문 상세 조회 | GET | /admin/orders/{id} | ❌ | ✅ | 관리자 전용 |
| 주문 상태 변경 | PATCH | /admin/orders/{id}/status | ❌ | ✅ | 관리자 전용 |
| **헬스체크** | | | | | |
| 헬스체크 | GET | /health | ✅ | ✅ | 인증 불필요 |

## 예제 계정

### 관리자 계정
```
이메일: admin@example.com
비밀번호: password123
권한: ROLE_ADMIN
```
**주의**: 관리자 계정은 모든 데이터에 접근 가능하므로 신중히 사용하세요.

### 일반 사용자 계정
```
이메일: user1@example.com ~ user15@example.com
비밀번호: password123 (모두 동일)
권한: ROLE_USER
{
  "email": "test@example.com",
  "password": "12345678"
}
```

## 엔드포인트 목록

총 **44개** 엔드포인트 구현

| 도메인 | 엔드포인트 수 |
|--------|--------------|
| Auth | 5개 |
| Users | 5개 |
| Admin Users | 4개 |
| Books | 2개 |
| Admin Books | 4개 |
| Reviews | 5개 |
| Admin Reviews | 2개 |
| Favorites | 3개 |
| Cart | 5개 |
| Orders | 4개 |
| Admin Orders | 3개 |
| Health | 1개 |
| Test | 1개 |

상세 API 명세는 [API 설계 문서](docs/api-design.md)를 참고하세요.

## API 응답 형식

### 성공 응답
```json
{
  "isSuccess": true,
  "message": "요청이 성공적으로 처리되었습니다",
  "code": null,
  "payload": {
    // 응답 데이터
    ...
  }
}
```

### 페이지네이션 응답
```json
{
  "isSuccess": true,
  "message": "조회 성공",
  "code": null,
  "payload": {
    "content": [],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 100,
    "totalPages": 5
  }
}
```

### 에러 응답
```json
{
  "timestamp": "2025-12-13T12:34:56Z",
  "path": "/api/v1/books/999",
  "status": 404,
  "code": "RESOURCE_NOT_FOUND",
  "message": "요청한 리소스를 찾을 수 없습니다",
  "details": {}
}
```

## 에러 코드

| HTTP | Code | Description |
|------|------|-------------|
| 400 | BAD_REQUEST | 잘못된 요청 |
| 400 | VALIDATION_FAILED | 입력값 검증 실패 |
| 400 | INVALID_QUERY_PARAM | 쿼리 파라미터 오류 |
| 400 | INVALID_INPUT_VALUE | 입력값 오류 |
| 401 | UNAUTHORIZED | 인증 필요 |
| 401 | TOKEN_EXPIRED | 토큰 만료 |
| 403 | FORBIDDEN | 접근 권한 없음 |
| 404 | RESOURCE_NOT_FOUND | 리소스 없음 |
| 404 | USER_NOT_FOUND | 사용자 없음 |
| 405 | METHOD_NOT_ALLOWED | HTTP 메서드 미지원 |
| 409 | DUPLICATE_RESOURCE | 중복 리소스 |
| 409 | STATE_CONFLICT | 상태 충돌 |
| 422 | UNPROCESSABLE_ENTITY | 처리 불가능 |
| 429 | TOO_MANY_REQUESTS | 요청 한도 초과 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |
| 500 | DATABASE_ERROR | 데이터베이스 오류 |
| 500 | UNKNOWN_ERROR | 알 수 없는 오류 |

## 성능/보안 고려사항

### 보안
1. **JWT 인증**: Access Token (15분) + Refresh Token (1시간)
2. **비밀번호 해싱**: BCrypt (strength=10)
3. **토큰 블랙리스트**: Redis 기반 로그아웃 토큰 관리
4. **CORS 설정**: 허용 도메인 명시
5. **레이트 리미팅**: 로그인/회원가입 API (분당 30회)
6. **입력 검증**: `@Valid` 어노테이션을 통한 DTO 검증
7. **계정 보안**: 비활성화 계정 API 접근 차단

### 성능
1. **N+1 문제 방지**: `@EntityGraph`를 통한 즉시 로딩
2. **페이지네이션**: 대량 데이터 조회 최적화
3. **인덱스**: 검색/조인 대상 컬럼에 인덱스 생성
   - books(title, author, category)
   - orders(status, created_at)
4. **Connection Pool**: HikariCP 최적화
5. **캐싱**: Redis를 통한 토큰 관리

### 데이터베이스
1. **외래키 제약조건**: 참조 무결성 보장
2. **Unique 제약조건**: 중복 데이터 방지
3. **CHECK 제약조건**: 도메인 무결성
4. **Flyway 마이그레이션**: 버전 관리
5. **시드 데이터**: 234건 (테스트/검증용)

## 테스트

### 자동화 테스트
- **총 46개** 테스트 케이스
- **통합 테스트**: MockMvc + @SpringBootTest
- **커버리지**: 주요 비즈니스 로직

### 테스트 실행
```bash
# 전체 테스트 실행
./gradlew test

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 테스트 목록
- AuthControllerTest (인증)
- UserMeControllerTest (사용자)
- AdminUserControllerTest (관리자-사용자)
- BookControllerTest (도서)
- AdminBookControllerTest (관리자-도서)
- ReviewControllerTest (리뷰)
- FavoriteControllerTest (찜)
- CartControllerTest (장바구니)
- OrderControllerTest (주문)
- AdminOrderControllerTest (관리자-주문)

## 문서

### API 문서
- **Swagger UI (Local)**: `http://localhost:8080/swagger-ui/index.html`
- **Swagger UI (Production)**: `http://113.198.66.68:10088/swagger-ui/index.html`
- **API 설계**: [docs/api-design.md](docs/api-design.md)

### 기술 문서
- **아키텍처**: [docs/architecture.md](docs/architecture.md)
- **DB 스키마**: [docs/db-schema.md](docs/db-schema.md)

## 프로젝트 구조

```
bookstore-api/
├── src/
│   ├── main/
│   │   ├── java/com/wsd/bookstoreapi/
│   │   │   ├── domain/          # 도메인별 비즈니스 로직
│   │   │   │   ├── auth/
│   │   │   │   ├── user/
│   │   │   │   ├── book/
│   │   │   │   ├── review/
│   │   │   │   ├── favorite/
│   │   │   │   ├── cart/
│   │   │   │   └── order/
│   │   │   └── global/          # 전역 설정
│   │   │       ├── api/
│   │   │       ├── config/
│   │   │       ├── error/
│   │   │       ├── security/
│   │   │       ├── logging/
│   │   │       └── rate/
│   │   └── resources/
│   │       ├── db/migration/    # Flyway 마이그레이션
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       └── application-prod.yml
│   └── test/                    # 테스트 코드
├── docs/                        # 문서
│   ├── api-design.md
│   ├── architecture.md
│   └── db-schema.md
├── .env.example                 # 환경변수 예제
├── .gitignore
├── docker-compose.yml           # Docker Compose 설정
├── Dockerfile                   # Docker 이미지 빌드
├── build.gradle                 # Gradle 빌드 설정
└── README.md
```

## DB 연결 정보

### Docker Compose 사용 시
```
Host: mysql (컨테이너명)
Port: 3306
Database: bookstore
Username: bookstore_user
Password: .env 파일에 설정된 값
```

### 로컬 MySQL 사용 시
```
Host: localhost
Port: 3306
Database: bookstore
Username: root (또는 별도 생성한 사용자)
Password: .env 파일에 설정된 값
```

## 한계와 개선 계획

### 현재 한계
1. **확장성**: 단일 서버 구조
2. **파일 업로드**: 도서 이미지 업로드 미구현
3. **결제 시스템**: 실제 결제 연동 없음
4. **알림 기능**: 주문 상태 변경 알림 없음
5. **재고 관리**: 동시성 제어 미흡

### 개선 계획
1. **MSA 전환**: 도메인별 마이크로서비스 분리
2. **이미지 스토리지**: S3 연동
3. **결제 연동**: PG사 API 연동
4. **메시징**: Kafka/RabbitMQ 기반 이벤트 처리
5. **동시성 제어**: Redis 분산 락 적용
6. **모니터링**: Prometheus + Grafana
7. **로깅**: ELK Stack 구축
8. **CI/CD**: GitHub Actions 자동 배포

## 라이선스

This project is licensed under the MIT License.

## 문의

프로젝트 관련 문의사항은 [Issues](https://github.com/your-username/bookstore-api/issues)에 등록해주세요.
