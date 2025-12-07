package com.wsd.bookstoreapi.support;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public User createAdminUser() {
        return userRepository.findByEmail("admin@test.com")
                .orElseGet(() -> {
                    User user = User.builder()
                            .email("admin@test.com")
                            .password(passwordEncoder.encode("P@ssw0rd!"))
                            .name("테스트 관리자")
                            .role("ROLE_ADMIN")   // 실제 enum/필드에 맞게 수정
                            .status("ACTIVE")
                            .build();
                    return userRepository.save(user);
                });
    }

    public User createNormalUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .password(passwordEncoder.encode("P@ssw0rd!"))
                            .name("일반 사용자")
                            .role("ROLE_USER")
                            .status("ACTIVE")
                            .build();
                    return userRepository.save(user);
                });
    }

    public Book createSampleBook(String title) {
        Book book = Book.builder()
                .title(title)
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .isbn("TEST-" + System.currentTimeMillis())
                .category("TEST_CATEGORY")
                .price(BigDecimal.valueOf(30000))
                .stockQuantity(10)
                .is_active(true)
                .build();
        return bookRepository.save(book);
    }
}
