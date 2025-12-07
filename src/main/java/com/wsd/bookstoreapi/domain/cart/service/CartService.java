package com.wsd.bookstoreapi.domain.cart.service;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.cart.dto.CartResponse;
import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import com.wsd.bookstoreapi.domain.cart.repository.CartItemRepository;
import com.wsd.bookstoreapi.domain.cart.repository.CartRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.SecurityUtil;
import com.wsd.bookstoreapi.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public CartResponse addItem(Long bookId, int quantity) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    if (newCart.getItems() == null) {
                        newCart.setItems(new ArrayList<>());
                    }

                    return cartRepository.save(newCart);
                });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // 장바구니에 같은 책이 이미 있는지 확인
        CartItem existingItem = cart.getItems().stream()
                .filter(ci -> ci.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(quantity)
                    .build();

            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return CartResponse.from(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    newCart.setItems(new ArrayList<>());
                    return newCart;
                });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return CartResponse.from(cart);
    }
    @Transactional
    public CartResponse updateItemQuantity(Long itemId, int quantity) {
        Long currentUserId = getCurrentUserId();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "장바구니 항목을 찾을 수 없습니다."
                        )
                );

        // 소유자 검증
        Cart cart = item.getCart();
        if (!cart.getUser().getId().equals(currentUserId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "본인의 장바구니 항목만 수정할 수 있습니다."
            );
        }

        // 수량 변경 (덮어쓰기 방식)
        item.setQuantity(quantity);
        // JPA 영속 상태라면 save()는 생략 가능하지만, 명시적으로 적어도 무방
        cartItemRepository.save(item);

        // 변경된 장바구니 전체를 응답
        return toCartResponse(cart);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        return principal.getUserId();
    }

    private CartResponse toCartResponse(Cart cart) {
        // 프로젝트에 이미 CartResponse.from(cart) 같은 팩토리가 있다면 그걸 사용
        return CartResponse.from(cart);
    }

}
