package com.wsd.bookstoreapi.domain.favorite.controller;

import com.wsd.bookstoreapi.domain.favorite.dto.FavoriteResponse;
import com.wsd.bookstoreapi.domain.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 내 찜 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites() {
        List<FavoriteResponse> list = favoriteService.getMyFavorites();
        return ResponseEntity.ok(list);
    }

    /**
     * 도서 찜 추가
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long bookId) {
        favoriteService.addFavorite(bookId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 도서 찜 해제
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long bookId) {
        favoriteService.removeFavorite(bookId);
        return ResponseEntity.noContent().build();
    }
}
