package dev.sndsprfct.orders.repository;

import dev.sndsprfct.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllOrdersByCustomerId(Long customerId);
}
