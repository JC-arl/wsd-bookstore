-- 예시: 기본 유저 2명 (user/admin)
INSERT INTO users (email, name, password, role, provider, status, created_at, updated_at)
VALUES
    ('user1@example.com', '일반유저1', '$2a$10$...', 'ROLE_USER', 'LOCAL', 'ACTIVE', NOW(), NOW()),
    ('admin@example.com', '관리자', '$2a$10$...', 'ROLE_ADMIN', 'LOCAL', 'ACTIVE', NOW(), NOW());

-- 예시: 책 여러 권 (실제 과제에서는 최소 50~100권 넣는 걸 추천)
INSERT INTO books (title, author, publisher, isbn, category, price, stock_quantity, description, published_at, active, created_at, updated_at)
VALUES
    ('자바의 정석', '남궁성', '도우출판', 'ISBN-0001', 'PROGRAMMING', 30000, 100, '자바 입문서', '2020-01-01', true, NOW(), NOW()),
    ('스프링 인 액션', '크레이그 월즈', '한빛미디어', 'ISBN-0002', 'PROGRAMMING', 40000, 50, '스프링 프레임워크', '2021-03-01', true, NOW(), NOW());
