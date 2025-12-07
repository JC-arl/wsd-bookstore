package com.wsd.bookstoreapi.support;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.repository.CartRepository;
import com.wsd.bookstoreapi.domain.user.entity.AuthProvider;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.entity.UserRole;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataFactory {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;

    public TestDataFactory(UserRepository userRepository,
                           BookRepository bookRepository,
                           PasswordEncoder passwordEncoder,
                           CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
    }

    public User createAdminUser() {
        return userRepository.findByEmail("admin@example.com")
                .orElseGet(() -> {
                    User user = User.builder()
                            .email("admin@example.com")
                            .password(passwordEncoder.encode("1q2w3e4r"))
                            .name("테스트 관리자")
                            .role(UserRole.ROLE_ADMIN)
                            .provider(AuthProvider.LOCAL)
                            .providerId(null)
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
                            .password(passwordEncoder.encode("1q2w3e4r"))
                            .name("일반 사용자")
                            .role(UserRole.ROLE_USER)
                            .provider(AuthProvider.LOCAL)
                            .providerId(null)
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

    /**
     * 관리자 도서 테스트용: 특정 ISBN으로 도서 생성
     */
    public Book createBookWithIsbn(String isbn, String title, String category) {
        Book book = Book.builder()
                .title(title)
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .isbn(isbn)                     // ★ 원하는 ISBN 직접 지정
                .category(category)
                .price(BigDecimal.valueOf(25000))
                .stockQuantity(5)
                .is_active(true)
                .build();

        return bookRepository.save(book);
    }

    /**
     * 도서 목록 검색용 더미 데이터 (이미 수정해둔 버전 유지)
     */
    public List<Book> createSampleBooksForSearch() {
        List<Book> books = new ArrayList<>();

        long now = System.currentTimeMillis();

        String isbn1 = "T" + (now % 1_000_000_000L);
        String isbn2 = "T" + ((now + 1) % 1_000_000_000L);
        String isbn3 = "T" + ((now + 2) % 1_000_000_000L);

        books.add(Book.builder()
                .title("이펙티브 자바")
                .author("조슈아 블로크")
                .publisher("인사이트")
                .isbn(isbn1)
                .category("PROGRAMMING")
                .price(BigDecimal.valueOf(30000))
                .stockQuantity(5)
                .is_active(true)
                .build()
        );

        books.add(Book.builder()
                .title("자바의 정석")
                .author("남궁성")
                .publisher("도우출판")
                .isbn(isbn2)
                .category("PROGRAMMING")
                .price(BigDecimal.valueOf(28000))
                .stockQuantity(3)
                .is_active(true)
                .build()
        );

        books.add(Book.builder()
                .title("미분적분학 개론")
                .author("홍길동")
                .publisher("수학출판사")
                .isbn(isbn3)
                .category("MATH")
                .price(BigDecimal.valueOf(25000))
                .stockQuantity(10)
                .is_active(true)
                .build()
        );

        return bookRepository.saveAll(books);
    }

    public Cart createCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

}
