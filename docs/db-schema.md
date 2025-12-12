# Database Schema 문서

## 목차
- [ERD 개요](#erd-개요)
- [테이블 상세](#테이블-상세)
- [관계 설명](#관계-설명)
- [인덱스 전략](#인덱스-전략)
- [제약조건](#제약조건)

## ERD 개요

### 엔티티 관계도 (텍스트 형식)

```
┌──────────────┐
│    users     │
│ (사용자)      │
└──────┬───────┘
       │ 1
       │
       ├──────────────┐
       │ N            │ N
┌──────▼────────┐ ┌──▼──────────┐
│   orders      │ │  reviews    │
│  (주문)       │ │  (리뷰)     │
└──────┬────────┘ └──────┬──────┘
       │ 1               │ N
       │ N               │ N
┌──────▼────────┐ ┌──────▼──────────┐
│ order_items   │ │     books       │
│ (주문상세)    │ │    (도서)       │
└───────────────┘ └──────┬──────────┘
                         │ 1
                         ├─────────────┐
                         │ N           │ N
                  ┌──────▼────────┐ ┌─▼──────────┐
                  │  cart_items   │ │ favorites  │
                  │ (장바구니항목)│ │   (찜)     │
                  └──────┬────────┘ └────────────┘
                         │ N
                         │ 1
                  ┌──────▼────────┐
                  │     cart      │
                  │   (장바구니)  │
                  └───────────────┘
```

## 테이블 상세

### 1. users (사용자)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 사용자 ID |
| email | VARCHAR(100) | NO | UK | - | 이메일 (로그인 ID) |
| name | VARCHAR(100) | NO | - | - | 사용자 이름 |
| password | VARCHAR(255) | YES | - | NULL | 비밀번호 (BCrypt) |
| role | VARCHAR(20) | NO | - | 'ROLE_USER' | 권한 |
| provider | VARCHAR(20) | NO | - | 'LOCAL' | 인증 제공자 |
| status | VARCHAR(20) | NO | - | 'ACTIVE' | 계정 상태 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 생성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- UNIQUE: email
- CHECK: role IN ('ROLE_USER', 'ROLE_ADMIN')
- CHECK: provider IN ('LOCAL', 'GOOGLE', 'KAKAO', 'NAVER')
- CHECK: status IN ('ACTIVE', 'INACTIVE')

**인덱스**:
- PRIMARY KEY (id)
- UNIQUE INDEX (email)

### 2. books (도서)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 도서 ID |
| title | VARCHAR(255) | NO | IDX | - | 도서 제목 |
| author | VARCHAR(100) | NO | IDX | - | 저자 |
| publisher | VARCHAR(100) | NO | - | - | 출판사 |
| isbn | VARCHAR(20) | NO | UK | - | ISBN |
| category | VARCHAR(50) | NO | - | - | 카테고리 |
| price | DECIMAL(10,2) | NO | - | 0 | 가격 |
| stock_quantity | INT | NO | - | 0 | 재고 수량 |
| description | TEXT | YES | - | NULL | 도서 설명 |
| published_at | DATE | YES | - | NULL | 출판일 |
| is_active | BOOLEAN | NO | - | TRUE | 활성화 여부 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- UNIQUE: isbn
- CHECK: price >= 0
- CHECK: stock_quantity >= 0

**인덱스**:
- PRIMARY KEY (id)
- UNIQUE INDEX (isbn)
- INDEX (title) - 검색 최적화
- INDEX (author) - 검색 최적화
- INDEX (category) - 필터링 최적화

**카테고리 목록**:
- PROGRAMMING, AI, DATABASE, CLOUD, DEVOPS
- MOBILE, COMPUTER_SCIENCE, DATA_ENGINEERING
- CERTIFICATE, ESSAY

### 3. orders (주문)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 주문 ID |
| user_id | BIGINT | NO | FK | - | 사용자 ID |
| status | VARCHAR(20) | NO | - | 'PENDING' | 주문 상태 |
| total_amount | DECIMAL(10,2) | NO | - | 0 | 총 금액 |
| shipping_address | VARCHAR(255) | NO | - | - | 배송 주소 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 주문일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (user_id) REFERENCES users(id)
- CHECK: status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELED', 'COMPLETED')
- CHECK: total_amount >= 0

**인덱스**:
- PRIMARY KEY (id)
- INDEX (user_id) - FK 자동 인덱스
- INDEX (status) - 상태 필터링 최적화
- INDEX (created_at) - 날짜 정렬 최적화

### 4. order_items (주문 상세)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 주문상세 ID |
| order_id | BIGINT | NO | FK | - | 주문 ID |
| book_id | BIGINT | NO | FK | - | 도서 ID |
| quantity | INT | NO | - | 1 | 수량 |
| unit_price | DECIMAL(10,2) | NO | - | 0 | 단가 |
| line_total | DECIMAL(10,2) | NO | - | 0 | 소계 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 생성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
- FOREIGN KEY (book_id) REFERENCES books(id)
- CHECK: quantity > 0
- CHECK: unit_price >= 0
- CHECK: line_total >= 0

**인덱스**:
- PRIMARY KEY (id)
- INDEX (order_id)
- INDEX (book_id)

### 5. reviews (리뷰)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 리뷰 ID |
| book_id | BIGINT | NO | FK | - | 도서 ID |
| user_id | BIGINT | NO | FK | - | 사용자 ID |
| rating | INT | NO | - | 5 | 평점 (1~5) |
| content | TEXT | YES | - | NULL | 리뷰 내용 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 작성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
- UNIQUE (user_id, book_id) - 한 사용자당 도서 1개 리뷰
- CHECK: rating BETWEEN 1 AND 5

**인덱스**:
- PRIMARY KEY (id)
- UNIQUE INDEX (user_id, book_id)
- INDEX (book_id) - 도서별 리뷰 조회 최적화

### 6. favorites (찜)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 찜 ID |
| user_id | BIGINT | NO | FK | - | 사용자 ID |
| book_id | BIGINT | NO | FK | - | 도서 ID |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 생성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
- FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
- UNIQUE (user_id, book_id) - 중복 찜 방지

**인덱스**:
- PRIMARY KEY (id)
- UNIQUE INDEX (user_id, book_id)

### 7. carts (장바구니)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 장바구니 ID |
| user_id | BIGINT | NO | FK, UK | - | 사용자 ID |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 생성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
- UNIQUE (user_id) - 사용자당 1개 장바구니

**인덱스**:
- PRIMARY KEY (id)
- UNIQUE INDEX (user_id)

### 8. cart_items (장바구니 항목)

| 컬럼명 | 타입 | NULL | 키 | 기본값 | 설명 |
|--------|------|------|-----|--------|------|
| id | BIGINT | NO | PK | AUTO_INCREMENT | 장바구니항목 ID |
| cart_id | BIGINT | NO | FK | - | 장바구니 ID |
| book_id | BIGINT | NO | FK | - | 도서 ID |
| quantity | INT | NO | - | 1 | 수량 |
| created_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP | 생성일시 |
| updated_at | TIMESTAMP | NO | - | CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**제약조건**:
- FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
- FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
- CHECK: quantity > 0

**인덱스**:
- PRIMARY KEY (id)
- INDEX (cart_id)
- INDEX (book_id)

## 관계 설명

### 1. users ↔ orders (1:N)
- 한 사용자는 여러 주문을 할 수 있음
- 사용자 삭제 시 주문은 유지 (참조 무결성)

### 2. orders ↔ order_items (1:N)
- 한 주문은 여러 주문 항목을 가짐
- 주문 삭제 시 주문 항목도 삭제 (CASCADE)

### 3. books ↔ order_items (1:N)
- 한 도서는 여러 주문 항목에 포함될 수 있음

### 4. users ↔ reviews (1:N)
- 한 사용자는 여러 리뷰를 작성 가능
- 사용자 삭제 시 리뷰도 삭제 (CASCADE)

### 5. books ↔ reviews (1:N)
- 한 도서는 여러 리뷰를 받을 수 있음
- 도서 삭제 시 리뷰도 삭제 (CASCADE)
- **제약**: 한 사용자당 도서 1개 리뷰만 가능 (UNIQUE)

### 6. users ↔ favorites (1:N)
- 한 사용자는 여러 도서를 찜할 수 있음
- 사용자 삭제 시 찜도 삭제 (CASCADE)

### 7. books ↔ favorites (1:N)
- 한 도서는 여러 사용자에게 찜될 수 있음
- 도서 삭제 시 찜도 삭제 (CASCADE)
- **제약**: 중복 찜 방지 (UNIQUE)

### 8. users ↔ carts (1:1)
- 한 사용자는 1개의 장바구니를 가짐
- 사용자 삭제 시 장바구니도 삭제 (CASCADE)

### 9. carts ↔ cart_items (1:N)
- 한 장바구니는 여러 항목을 가질 수 있음
- 장바구니 삭제 시 항목도 삭제 (CASCADE)

### 10. books ↔ cart_items (1:N)
- 한 도서는 여러 장바구니에 담길 수 있음
- 도서 삭제 시 장바구니 항목도 삭제 (CASCADE)

## 인덱스 전략

### Primary Key Indexes
모든 테이블의 `id` 컬럼에 자동 생성

### Foreign Key Indexes
- `orders.user_id`
- `order_items.order_id`
- `order_items.book_id`
- `reviews.user_id`
- `reviews.book_id`
- `favorites.user_id`
- `favorites.book_id`
- `carts.user_id`
- `cart_items.cart_id`
- `cart_items.book_id`

### Unique Indexes
- `users.email` - 로그인 ID 중복 방지
- `books.isbn` - ISBN 중복 방지
- `reviews(user_id, book_id)` - 리뷰 중복 방지
- `favorites(user_id, book_id)` - 찜 중복 방지
- `carts.user_id` - 장바구니 1:1 관계

### Performance Indexes
- `books.title` - 제목 검색 최적화
- `books.author` - 저자 검색 최적화
- `books.category` - 카테고리 필터링 최적화
- `orders.status` - 주문 상태 필터링
- `orders.created_at` - 주문 날짜 정렬

## 제약조건

### CHECK 제약조건

```sql
-- users
CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
CHECK (provider IN ('LOCAL', 'GOOGLE', 'KAKAO', 'NAVER'))
CHECK (status IN ('ACTIVE', 'INACTIVE'))

-- books
CHECK (price >= 0)
CHECK (stock_quantity >= 0)

-- orders
CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELED', 'COMPLETED'))
CHECK (total_amount >= 0)

-- order_items
CHECK (quantity > 0)
CHECK (unit_price >= 0)
CHECK (line_total >= 0)

-- reviews
CHECK (rating BETWEEN 1 AND 5)

-- cart_items
CHECK (quantity > 0)
```

### CASCADE 규칙

**ON DELETE CASCADE**:
- `order_items` → `orders`
- `reviews` → `users`, `books`
- `favorites` → `users`, `books`
- `carts` → `users`
- `cart_items` → `carts`, `books`

## 마이그레이션 관리

### Flyway 마이그레이션 파일

1. **V1__baseline.sql**
   - 모든 테이블 생성
   - 인덱스 생성
   - 제약조건 정의

2. **V2__seed_data.sql**
   - 초기 데이터 삽입
   - 관리자 계정 (admin@example.com)
   - 일반 사용자 15명
   - 도서 100권
   - 리뷰 100건
   - 샘플 주문/장바구니/찜 데이터

### 데이터 통계 (시드 데이터)

| 테이블 | 레코드 수 | 설명 |
|--------|----------|------|
| users | 16 | 관리자 1명 + 사용자 15명 |
| books | 100 | 다양한 카테고리 도서 |
| reviews | 100 | 사용자별 리뷰 |
| orders | 2 | 샘플 주문 |
| order_items | 4 | 주문 상세 |
| favorites | 5 | 샘플 찜 |
| carts | 2 | 샘플 장바구니 |
| cart_items | 5 | 장바구니 항목 |
| **총계** | **234건** | |

## 데이터 무결성

1. **참조 무결성**: 외래키 제약조건으로 보장
2. **도메인 무결성**: CHECK 제약조건으로 보장
3. **엔티티 무결성**: PRIMARY KEY로 보장
4. **사용자 정의 무결성**: UNIQUE 제약조건으로 보장

## N+1 문제 해결

JPA에서 발생하는 N+1 문제는 다음 방법으로 해결:

1. **@EntityGraph** 사용
2. **Fetch Join** 쿼리
3. **DTO Projection** 활용
4. **Lazy Loading** 전략

## 확장 고려사항

향후 확장 시 고려할 사항:

1. **파티셔닝**: orders 테이블 날짜별 파티셔닝
2. **샤딩**: 사용자 기반 샤딩
3. **Read Replica**: 읽기 성능 향상
4. **캐싱**: 자주 조회되는 데이터 Redis 캐싱
