package com.wsd.bookstoreapi.global.config;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
@Profile("local") // 로컬에서만 시드하고 싶을 때
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        Random random = new Random();

        for (int i = 1; i <= 100; i++) {
            Book book = Book.builder()
                    .title("샘플 도서 " + i)
                    .author("저자 " + i)
                    .publisher("출판사 " + (i % 10))
                    .isbn("SAMPLE-" + i)
                    .category("CATEGORY_" + (i % 5))
                    .price(BigDecimal.valueOf(10000 + random.nextInt(30000)))
                    .stockQuantity(10 + random.nextInt(90))
                    .description("샘플 설명 " + i)
                    .publishedAt(LocalDate.of(2020, 1, 1).plusDays(i))
                    .is_active(true)
                    .build();

            bookRepository.save(book);
        }
    }
}
