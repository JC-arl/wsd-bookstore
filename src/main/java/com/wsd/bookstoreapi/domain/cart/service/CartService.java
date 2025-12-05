package com.wsd.bookstoreapi.domain.cart.service;

import com.wsd.bookstoreapi.domain.book.entity.Book;
import com.wsd.bookstoreapi.domain.book.repository.BookRepository;
import com.wsd.bookstoreapi.domain.cart.dto.CartItemRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addItemToCart(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다."));

        // 이미 같은 책이 있으면 수량만 증가
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }

        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED, "수량은 1 이상이어야 합니다.");
        }

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "장바구니 항목을 찾을 수 없습니다."));

        item.setQuantity(quantity);

        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse removeCartItem(Long itemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));

        if (!removed) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "장바구니 항목을 찾을 수 없습니다.");
        }

        return CartResponse.from(cart);
    }

    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
    }
}
