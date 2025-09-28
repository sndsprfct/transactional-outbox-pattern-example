package dev.sndsprfct.orders.repository;

import dev.sndsprfct.orders.constant.OrderStatus;
import dev.sndsprfct.orders.entity.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllOrdersByCustomerId(Long customerId);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    void updateOrderStatus(Long orderId, OrderStatus status);
}
