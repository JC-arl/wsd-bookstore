package com.wsd.bookstoreapi.support;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.user.entity.AuthProvider;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.entity.UserRole;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestDataFactory {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataFactory(UserRepository userRepository,
                           BookRepository bookRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createAdminUser() {
        return userRepository.findByEmail("admin@example.com")  // ← 테스트 코드와 맞추기
                .orElseGet(() -> {
                    User user = User.builder()
                            .email("admin@example.com")
                            .password(passwordEncoder.encode("1q2w3e4r"))
                            .name("테스트 관리자")
                            .role(UserRole.ROLE_ADMIN)
                            .provider(AuthProvider.LOCAL)
                            .providerId(null)
                            .status("ACTIVE")        // enum이면 UserStatus.ACTIVE로 변경
                            .build();
                    return userRepository.save(user);
                });
    }

    public User createNormalUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .password(passwordEncoder.encode("1q2w3e4r"))
                            .name("일반 사용자")
                            .role(UserRole.ROLE_USER)
                            .provider(AuthProvider.LOCAL)
                            .providerId(null)
                            .status("ACTIVE")    // enum 타입이면 여기도 수정
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
