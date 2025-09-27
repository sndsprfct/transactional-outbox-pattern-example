package dev.sndsprfct.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sndsprfct.orders.constant.OutboxEventType;
import dev.sndsprfct.orders.entity.Order;
import dev.sndsprfct.orders.entity.outbox.OutboxEvent;
import dev.sndsprfct.orders.exception.OutboxEventPayloadWasNotSerializedException;
import dev.sndsprfct.orders.repository.OrderRepository;
import dev.sndsprfct.orders.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long processOrderCreation(Order order) {
        Order savedOrder = orderRepository.save(order);
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setType(OutboxEventType.ORDER_CREATED);
        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(savedOrder));
        } catch (JsonProcessingException e) {
            throw new OutboxEventPayloadWasNotSerializedException(e);
        }
        outboxEventRepository.save(outboxEvent);
        return savedOrder.getId();
    }
}
