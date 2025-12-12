# API 설계 문서

## 목차
- [개요](#개요)
- [엔드포인트 목록](#엔드포인트-목록)
- [인증/인가](#인증인가)
- [요청/응답 형식](#요청응답-형식)
- [에러 처리](#에러-처리)

## 개요

본 API는 온라인 서점 시스템을 위한 RESTful API입니다.

- **Base URL**: `http://{SERVER_URL}:{PORT}/api/v1`
- **인증 방식**: JWT Bearer Token
- **응답 형식**: JSON
- **문자 인코딩**: UTF-8

## 엔드포인트 목록

### 1. 인증 (Auth)

| HTTP | Path | Description | Auth |
|------|------|-------------|------|
| POST | /auth/signup | 회원가입 | 불필요 |
| POST | /auth/login | 로그인 | 불필요 |
| POST | /auth/refresh | 토큰 갱신 | 불필요 |
| POST | /auth/logout | 로그아웃 | 필요 |
| GET | /auth/test-bcrypt | BCrypt 해시 테스트 | 불필요 |

### 2. 사용자 (Users)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /users/me | 내 정보 조회 | 필요 | USER |
| PATCH | /users/me | 내 정보 수정 | 필요 | USER |
| PATCH | /users/me/deactivate | 계정 비활성화 | 필요 | USER |
| PATCH | /users/me/activate | 계정 활성화 | 필요 | USER |
| DELETE | /users/me | 계정 영구 삭제 | 필요 | USER |

### 3. 관리자 - 사용자 관리 (Admin Users)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /admin/users | 사용자 목록 조회 | 필요 | ADMIN |
| GET | /admin/users/{id} | 사용자 상세 조회 | 필요 | ADMIN |
| PATCH | /admin/users/{id}/deactivate | 사용자 비활성화 | 필요 | ADMIN |
| PATCH | /admin/users/{id}/activate | 사용자 활성화 | 필요 | ADMIN |

### 4. 도서 (Books)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /books | 도서 목록 조회 (검색/페이지네이션) | 불필요 | - |
| GET | /books/{id} | 도서 상세 조회 | 불필요 | - |

**검색 파라미터**:
- `keyword`: 제목/저자/출판사로 검색
- `category`: 카테고리 필터
- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본 20)
- `sort`: 정렬 (예: `createdAt,DESC`)

### 5. 관리자 - 도서 관리 (Admin Books)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| POST | /admin/books | 도서 등록 | 필요 | ADMIN |
| PATCH | /admin/books/{id} | 도서 정보 수정 | 필요 | ADMIN |
| PATCH | /admin/books/{id}/activate | 도서 활성화/비활성화 | 필요 | ADMIN |
| DELETE | /admin/books/{id} | 도서 삭제 | 필요 | ADMIN |

### 6. 리뷰 (Reviews)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| POST | /books/{bookId}/reviews | 리뷰 작성 | 필요 | USER |
| GET | /books/{bookId}/reviews | 도서별 리뷰 목록 | 불필요 | - |
| GET | /reviews/me | 내가 쓴 리뷰 목록 | 필요 | USER |
| PATCH | /books/{bookId}/reviews/me | 내 리뷰 수정 | 필요 | USER |
| DELETE | /books/{bookId}/reviews/me | 내 리뷰 삭제 | 필요 | USER |

### 7. 관리자 - 리뷰 관리 (Admin Reviews)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /admin/reviews | 전체 리뷰 목록 조회 | 필요 | ADMIN |
| DELETE | /admin/reviews/{id} | 리뷰 삭제 | 필요 | ADMIN |

### 8. 찜 (Favorites)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /favorites | 내 찜 목록 조회 | 필요 | USER |
| POST | /favorites/{bookId} | 도서 찜 추가 | 필요 | USER |
| DELETE | /favorites/{bookId} | 도서 찜 해제 | 필요 | USER |

### 9. 장바구니 (Cart)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /cart | 내 장바구니 조회 | 필요 | USER |
| POST | /cart/items | 장바구니 항목 추가 | 필요 | USER |
| PATCH | /cart/items/book/{bookId} | 장바구니 항목 수량 변경 | 필요 | USER |
| DELETE | /cart/items/book/{bookId} | 장바구니 아이템 삭제 | 필요 | USER |
| DELETE | /cart | 장바구니 전체 비우기 | 필요 | USER |

### 10. 주문 (Orders)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| POST | /orders | 주문 생성 (장바구니 기반) | 필요 | USER |
| GET | /orders | 내 주문 목록 조회 | 필요 | USER |
| GET | /orders/{id} | 내 주문 상세 조회 | 필요 | USER |
| PATCH | /orders/{id}/cancel | 내 주문 취소 | 필요 | USER |

**필터 파라미터**:
- `status`: 주문 상태 필터 (PENDING, SHIPPED, DELIVERED, CANCELED 등)

### 11. 관리자 - 주문 관리 (Admin Orders)

| HTTP | Path | Description | Auth | Role |
|------|------|-------------|------|------|
| GET | /admin/orders | 전체 주문 목록 조회 | 필요 | ADMIN |
| GET | /admin/orders/{id} | 주문 상세 조회 | 필요 | ADMIN |
| PATCH | /admin/orders/{id}/status | 주문 상태 변경 | 필요 | ADMIN |

### 12. 헬스체크

| HTTP | Path | Description | Auth |
|------|------|-------------|------|
| GET | /health | 서버 상태 확인 | 불필요 |

**총 엔드포인트 수: 44개**

## 인증/인가

### JWT 기반 인증

1. **로그인**: `POST /auth/login`
   - 이메일/비밀번호로 로그인
   - 성공 시 Access Token, Refresh Token 반환

2. **인증 헤더 형식**:
   ```
   Authorization: Bearer {access_token}
   ```

3. **토큰 갱신**: `POST /auth/refresh`
   - Refresh Token으로 새로운 Access Token 발급

4. **로그아웃**: `POST /auth/logout`
   - 토큰 블랙리스트 등록

### 역할 기반 접근 제어 (RBAC)

- **ROLE_USER**: 일반 사용자
  - 도서 조회, 리뷰 작성, 주문, 찜, 장바구니 관리

- **ROLE_ADMIN**: 관리자
  - 모든 USER 권한 + 도서/사용자/주문/리뷰 관리

## 요청/응답 형식

### 성공 응답

```json
{
  "isSuccess": true,
  "message": "요청이 성공했습니다",
  "code": null,
  "payload": {
    // 응답 데이터
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
    "content": [ /* 데이터 배열 */ ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

## 에러 처리

### 에러 응답 형식

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

### 표준 에러 코드

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
| 422 | UNPROCESSABLE_ENTITY | 처리 불가능한 요청 |
| 429 | TOO_MANY_REQUESTS | 요청 한도 초과 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |
| 500 | DATABASE_ERROR | 데이터베이스 오류 |
| 500 | UNKNOWN_ERROR | 알 수 없는 오류 |

## API 변경 이력

### v1.0.0 (2025-12-13)
- 초기 API 설계 및 구현
- 44개 엔드포인트 구현
- JWT 인증/인가
- 페이지네이션, 검색, 정렬 기능
