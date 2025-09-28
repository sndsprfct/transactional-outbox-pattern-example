package dev.sndsprfct.orders.service;

import dev.sndsprfct.orders.constant.OrderStatus;
import dev.sndsprfct.orders.constant.OutboxEventType;
import dev.sndsprfct.orders.entity.orders.Order;
import dev.sndsprfct.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    @Transactional
    public Long processOrderCreation(Order order) {
        Order savedOrder = orderRepository.save(order);
        outboxService.saveOutboxEvent(savedOrder, OutboxEventType.ORDER_CREATED);
        return savedOrder.getId();
    }

    @Transactional
    public void processOrderCancellation(Order order) {
        orderRepository.updateOrderStatus(order.getId(), OrderStatus.CANCELED);
        outboxService.saveOutboxEvent(order, OutboxEventType.ORDER_CANCELLED);
    }
}
