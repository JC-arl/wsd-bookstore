-- ============================
-- V1__baseline.sql
-- 스키마 초기 생성 (Baseline)
-- ============================

-- 사용자 테이블
CREATE TABLE users (
                       id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       name          VARCHAR(100) NOT NULL,
                       password      VARCHAR(255),             -- OAuth 계정은 null 가능
                       role          VARCHAR(20)  NOT NULL,   -- 예: ROLE_USER, ROLE_ADMIN
                       provider      VARCHAR(20)  NOT NULL,   -- 예: LOCAL, GOOGLE ...
                       provider_id   VARCHAR(100),
                       status        VARCHAR(20)  NOT NULL,   -- 예: ACTIVE, INACTIVE 등

                       created_at    DATETIME(6)  NOT NULL,
                       updated_at    DATETIME(6)  NOT NULL,

                       INDEX idx_users_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 도서 테이블
CREATE TABLE books (
                       id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title            VARCHAR(255) NOT NULL,
                       author           VARCHAR(255) NOT NULL,
                       publisher        VARCHAR(255),
                       isbn             VARCHAR(20) UNIQUE,
                       category         VARCHAR(100),
                       price            DECIMAL(12, 2) NOT NULL,
                       stock_quantity   INT NOT NULL,
                       description      TEXT,
                       published_at     DATE,
                       is_active        BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at       DATETIME(6)  NOT NULL,
                       updated_at       DATETIME(6)  NOT NULL,

                       INDEX idx_books_title (title),
                       INDEX idx_books_category (category)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 찜(Favorite) 테이블
CREATE TABLE favorites (
                           id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id    BIGINT NOT NULL,
                           book_id    BIGINT NOT NULL,

                           created_at DATETIME(6) NOT NULL,
                           updated_at DATETIME(6) NOT NULL,

                           CONSTRAINT fk_favorites_user
                               FOREIGN KEY (user_id) REFERENCES users (id),
                           CONSTRAINT fk_favorites_book
                               FOREIGN KEY (book_id) REFERENCES books (id),

                           UNIQUE KEY uk_favorites_user_book (user_id, book_id),
                           INDEX idx_favorites_user_id (user_id),
                           INDEX idx_favorites_book_id (book_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 장바구니(Cart) 테이블
CREATE TABLE carts (
                       id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id    BIGINT NOT NULL UNIQUE,

                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,

                       CONSTRAINT fk_carts_user
                           FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 장바구니 항목(CartItem) 테이블
CREATE TABLE cart_items (
                            id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                            cart_id    BIGINT NOT NULL,
                            book_id    BIGINT NOT NULL,
                            quantity   INT    NOT NULL,

                            created_at DATETIME(6) NOT NULL,
                            updated_at DATETIME(6) NOT NULL,

                            CONSTRAINT fk_cart_items_cart
                                FOREIGN KEY (cart_id) REFERENCES carts (id),
                            CONSTRAINT fk_cart_items_book
                                FOREIGN KEY (book_id) REFERENCES books (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 주문(Orders) 테이블
CREATE TABLE orders (
                        id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id          BIGINT       NOT NULL,
                        status           VARCHAR(20)  NOT NULL,  -- PENDING / COMPLETED / CANCELED 등
                        total_amount     DECIMAL(12, 2) NOT NULL,
                        shipping_address VARCHAR(500) NOT NULL,

                        created_at       DATETIME(6)   NOT NULL,
                        updated_at       DATETIME(6)   NOT NULL,

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id) REFERENCES users (id),

                        INDEX idx_orders_user_id   (user_id),
                        INDEX idx_orders_status (status),
                        INDEX idx_orders_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 주문 상세(OrderItem) 테이블
CREATE TABLE order_items (
                             id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id   BIGINT       NOT NULL,
                             book_id    BIGINT       NOT NULL,
                             quantity   INT          NOT NULL,
                             unit_price DECIMAL(12,2) NOT NULL,
                             line_total DECIMAL(12,2) NOT NULL,

                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6) NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id) REFERENCES orders (id),
                             CONSTRAINT fk_order_items_book
                                 FOREIGN KEY (book_id) REFERENCES books (id),

                             INDEX idx_order_items_order_id (order_id),
                             INDEX idx_order_items_book_id (book_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 리뷰(Review) 테이블
CREATE TABLE reviews (
                         id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id    BIGINT  NOT NULL,
                         book_id    BIGINT  NOT NULL,
                         rating     INT     NOT NULL,
                         content    TEXT,

                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL,

                         CONSTRAINT fk_reviews_user
                             FOREIGN KEY (user_id) REFERENCES users (id),
                         CONSTRAINT fk_reviews_book
                             FOREIGN KEY (book_id) REFERENCES books (id),

                         INDEX idx_reviews_book_id (book_id),
                         INDEX idx_reviews_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
