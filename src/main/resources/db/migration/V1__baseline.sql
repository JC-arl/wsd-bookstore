CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       provider VARCHAR(20) NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL
);

CREATE TABLE books (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publisher VARCHAR(255),
                       isbn VARCHAR(50),
                       category VARCHAR(50),
                       price DECIMAL(10,2) NOT NULL,
                       stock_quantity INT NOT NULL DEFAULT 10,
                       description TEXT,
                       published_at DATE,
                       active TINYINT(1) NOT NULL DEFAULT 1,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL
);
CREATE TABLE reviews (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         book_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         content TEXT,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,

                         CONSTRAINT fk_reviews_user
                             FOREIGN KEY (user_id) REFERENCES users(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_reviews_book
                             FOREIGN KEY (book_id) REFERENCES books(id)
                                 ON DELETE CASCADE
);

-- DROP DATABASE IF EXISTS bookstore;
-- CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--
-- CREATE USER 'bookstore_user'@'%' IDENTIFIED BY 'ghkdwlcks1!';
-- GRANT ALL PRIVILEGES ON bookstore.* TO 'bookstore_user'@'%';
-- FLUSH PRIVILEGES;

