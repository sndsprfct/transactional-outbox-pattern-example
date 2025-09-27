package dev.sndsprfct.orders.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.sndsprfct.orders.constant.OutboxEventStatus;
import dev.sndsprfct.orders.constant.OutboxEventType;
import dev.sndsprfct.orders.entity.Order;
import dev.sndsprfct.orders.entity.outbox.OutboxEvent;
import dev.sndsprfct.orders.repository.OrderRepository;
import dev.sndsprfct.orders.repository.OutboxEventRepository;
import dev.sndsprfct.orders.service.OrderProcessingService;
import dev.sndsprfct.orders.utils.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxEventArgumentCaptor;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @InjectMocks
    private OrderProcessingService orderProcessingService;

    @Test
    void processOrderCreation() {
        // given
        Order testOrder = TestConstants.getTestOrder();
        when(orderRepository.save(eq(testOrder)))
                .thenReturn(testOrder.toBuilder().id(TestConstants.TEST_ORDER_ID).build());
        OutboxEvent expectedOutboxEvent = OutboxEvent.builder()
                .status(OutboxEventStatus.PENDING)
                .type(OutboxEventType.ORDER_CREATED)
                .build();

        // when
        Long orderId = orderProcessingService.processOrderCreation(testOrder);

        // then
        assertThat(orderId)
                .isEqualTo(TestConstants.TEST_ORDER_ID);

        verify(orderRepository).save(testOrder);
        verify(outboxEventRepository).save(outboxEventArgumentCaptor.capture());
        OutboxEvent capturedOutboxEvent = outboxEventArgumentCaptor.getValue();

        assertThat(capturedOutboxEvent)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .ignoringFields("payload")
                .isEqualTo(expectedOutboxEvent);
        assertThat(capturedOutboxEvent.getPayload())
                .isNotBlank();
    }


}