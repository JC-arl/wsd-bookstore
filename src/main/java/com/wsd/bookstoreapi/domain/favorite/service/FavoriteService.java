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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<FavoriteResponse> getMyFavorites(Pageable pageable) {
        User user = getCurrentUser();
        return favoriteRepository.findByUser(user, pageable)
                .map(FavoriteResponse::from);
    }

    @Transactional
    public FavoriteResponse addFavorite(Long bookId) {
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
        return FavoriteResponse.from(favorite);
    }

    @Transactional
    public FavoriteResponse removeFavorite(Long bookId) {
        User user = getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        Favorite favorite = favoriteRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "찜 목록에서 해당 도서를 찾을 수 없습니다."));

        FavoriteResponse response = FavoriteResponse.from(favorite);
        favoriteRepository.deleteByUserAndBook(user, book);
        return response;
    }
}
