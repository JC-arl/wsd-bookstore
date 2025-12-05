package com.wsd.bookstoreapi.domain.favorite.service;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.favorite.dto.FavoriteResponse;
import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import com.wsd.bookstoreapi.domain.favorite.repository.FavoriteRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites() {
        User user = getCurrentUser();
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    @Transactional
    public void addFavorite(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        boolean exists = favoriteRepository.findByUserAndBook(user, book).isPresent();
        if (exists) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE, "이미 찜한 도서입니다.");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .book(book)
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        favoriteRepository.deleteByUserAndBook(user, book);
    }
}
