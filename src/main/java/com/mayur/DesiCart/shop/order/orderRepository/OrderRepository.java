package com.mayur.DesiCart.shop.order.orderRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.user.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    // Used by Webhook to find the order by Razorpay's ID
    Optional<Order> findByGatewayOrderId(String gatewayOrderId);

    // Finds an order for a user that matches any of the provided statuses
    Optional<Order> findByUserAndOrderStatusIn(User user, List<OrderStatus> statuses);

    boolean existsByUserAndOrderStatus(User user, OrderStatus status);

    Optional<Order> findTopByUserAndOrderStatusInOrderByCreatedAtDesc(
            User user,
            List<OrderStatus> statuses
    );
    List<Order> findByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime dateTime);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderStatus = :status AND o.createdAt < :cutoff")
    List<Order> findExpiredOrdersWithItems(@Param("status") OrderStatus status, @Param("cutoff") LocalDateTime cutoff);
}
