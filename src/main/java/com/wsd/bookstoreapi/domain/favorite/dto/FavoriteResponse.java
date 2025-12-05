package com.wsd.bookstoreapi.domain.favorite.dto;

import com.wsd.bookstoreapi.domain.favorite.entity.Favorite;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponse {

    private final Long id;
    private final Long bookId;
    private final String bookTitle;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .bookId(favorite.getBook().getId())
                .bookTitle(favorite.getBook().getTitle())
                .build();
    }
}
