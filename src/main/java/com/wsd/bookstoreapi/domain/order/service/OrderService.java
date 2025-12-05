package com.wsd.bookstoreapi.domain.order.service;

import com.wsd.bookstoreapi.domain.cart.entity.Cart;
import com.wsd.bookstoreapi.domain.cart.entity.CartItem;
import com.wsd.bookstoreapi.domain.cart.repository.CartRepository;
import com.wsd.bookstoreapi.domain.order.dto.OrderCreateRequest;
import com.wsd.bookstoreapi.domain.order.dto.OrderResponse;
import com.wsd.bookstoreapi.domain.order.dto.OrderStatusUpdateRequest;
import com.wsd.bookstoreapi.domain.order.entity.Order;
import com.wsd.bookstoreapi.domain.order.entity.OrderItem;
import com.wsd.bookstoreapi.domain.order.entity.OrderStatus;
import com.wsd.bookstoreapi.domain.order.repository.OrderRepository;
import com.wsd.bookstoreapi.domain.user.entity.User;
import com.wsd.bookstoreapi.domain.user.repository.UserRepository;
import com.wsd.bookstoreapi.global.error.BusinessException;
import com.wsd.bookstoreapi.global.error.ErrorCode;
import com.wsd.bookstoreapi.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    /**
     * 내 장바구니 기반 주문 생성
     */
    @Transactional
    public OrderResponse createOrderFromCart(OrderCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "장바구니가 비어 있습니다."));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT, "장바구니에 상품이 없습니다.");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            BigDecimal unitPrice = cartItem.getBook().getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();

            order.getOrderItems().add(orderItem);
            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);

        // 주문 저장
        Order saved = orderRepository.save(order);

        // 장바구니 비우기
        cart.getItems().clear();

        return OrderResponse.from(saved);
    }

    /**
     * 내 주문 목록 조회 (상태 필터 가능)
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(OrderStatus status, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Page<Order> page;
        if (status != null) {
            page = orderRepository.findByUserAndStatus(user, status, pageable);
        } else {
            page = orderRepository.findByUser(user, pageable);
        }

        return page.map(OrderResponse::from);
    }

    /**
     * 내 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "본인의 주문만 조회할 수 있습니다.");
        }

        return OrderResponse.from(order);
    }

    /**
     * 내 주문 취소
     */
    @Transactional
    public void cancelMyOrder(Long orderId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELED
                || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT, "해당 상태에서는 주문을 취소할 수 없습니다.");
        }

        order.setStatus(OrderStatus.CANCELED);
    }

    /**
     * 관리자용 - 전체 주문 목록
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersForAdmin(OrderStatus status, Pageable pageable) {
        Page<Order> page;

        if (status != null) {
            page = orderRepository.findByStatus(status, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        return page.map(OrderResponse::from);
    }

    /**
     * 관리자용 - 주문 상세
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));

        return OrderResponse.from(order);
    }

    /**
     * 관리자용 - 주문 상태 변경
     */
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));

        order.setStatus(request.getStatus());
    }
}
