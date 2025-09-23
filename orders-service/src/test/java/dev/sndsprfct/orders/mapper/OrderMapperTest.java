package dev.sndsprfct.orders.mapper;

import dev.sndsprfct.orders.constant.OrderStatus;
import dev.sndsprfct.orders.dto.request.OrderCreationRequestDto;
import dev.sndsprfct.orders.dto.response.OrderItemResponseDto;
import dev.sndsprfct.orders.dto.response.OrderResponseDto;
import dev.sndsprfct.orders.entity.Order;
import dev.sndsprfct.orders.entity.OrderItem;
import dev.sndsprfct.orders.entity.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class OrderMapperTest {

    private static final Long TEST_CUSTOMER_ID = 1L;
    private static final String TEST_DELIVERY_ADDRESS = "Delivery Address";
    private static final UUID TEST_IDEMPOTENCY_KEY = UUID.randomUUID();

    private static final Long TEST_PRODUCT1_ID = 1L;
    private static final String TEST_PRODUCT1_NAME = "Test Product 1";
    private static final Long TEST_PRODUCT1_PRICE = 100L;

    private static final Long TEST_PRODUCT2_ID = 2L;
    private static final String TEST_PRODUCT2_NAME = "Test Product 2";
    private static final Long TEST_PRODUCT2_PRICE = 100L;

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void testOrderEntityToOrderResponseDtoMapping() {
        // given
        Order order = getTestOrder();
        OrderResponseDto orderResponseDto = getOrderResponseDto();


        // when
        OrderResponseDto map = orderMapper.map(order);

        // then
        assertThat(map)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .isEqualTo(orderResponseDto);
    }

    @Test
    void testOrderCreationRequestDtoToOrderEntityMapping() {
        // given
        OrderCreationRequestDto orderCreationRequestDto = getOrderCreationRequestDto();
        Order expectedOrder = getTestOrder();
        expectedOrder.setId(null);
        List<Product> products = List.of(getProduct1(), getProduct2());

        // when
        Order order = orderMapper.map(orderCreationRequestDto, products);

        // then
        assertThat(order)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class, OrderItem.class)
                .isEqualTo(expectedOrder);

        assertThat(order.getOrderItems())
                .extracting("product.name", "quantity", "unitPrice")
                .containsExactlyInAnyOrder(
                        tuple(TEST_PRODUCT1_NAME, 2, TEST_PRODUCT1_PRICE),
                        tuple(TEST_PRODUCT2_NAME, 2, TEST_PRODUCT2_PRICE));
    }

    private OrderCreationRequestDto getOrderCreationRequestDto() {
        return new OrderCreationRequestDto(
                1L,
                TEST_IDEMPOTENCY_KEY,
                Map.of(TEST_PRODUCT1_ID, 2, TEST_PRODUCT2_ID, 2),
                TEST_DELIVERY_ADDRESS);
    }

    private OrderResponseDto getOrderResponseDto() {
        OrderItemResponseDto orderItemResponseDto1 = new OrderItemResponseDto(
                TEST_PRODUCT1_ID,
                TEST_PRODUCT1_ID,
                TEST_PRODUCT1_NAME,
                1,
                TEST_PRODUCT1_PRICE);
        OrderItemResponseDto orderItemResponseDto2 = new OrderItemResponseDto(
                TEST_PRODUCT2_ID,
                TEST_PRODUCT2_ID,
                TEST_PRODUCT2_NAME,
                1,
                TEST_PRODUCT2_PRICE);

        return new OrderResponseDto(
                1L,
                TEST_DELIVERY_ADDRESS,
                Instant.now(),
                OrderStatus.PENDING,
                List.of(orderItemResponseDto1, orderItemResponseDto2),
                TEST_PRODUCT1_PRICE + TEST_PRODUCT2_PRICE);
    }

    private Order getTestOrder() {
        Product product1 = getProduct1();
        Product product2 = getProduct2();

        OrderItem orderItem1 = OrderItem.builder()
                .id(1L)
                .product(product1)
                .quantity(1)
                .unitPrice(TEST_PRODUCT1_PRICE)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(2L)
                .product(product2)
                .quantity(1)
                .unitPrice(TEST_PRODUCT2_PRICE)
                .build();

        return Order.builder()
                .id(1L)
                .customerId(TEST_CUSTOMER_ID)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .idempotencyKey(TEST_IDEMPOTENCY_KEY)
                .deliveryAddress(TEST_DELIVERY_ADDRESS)
                .orderItems(List.of(orderItem1, orderItem2))
                .build();
    }

    private static Product getProduct2() {
        return Product.builder()
                .id(TEST_PRODUCT2_ID)
                .name(TEST_PRODUCT2_NAME)
                .price(TEST_PRODUCT2_PRICE)
                .isAvailable(true)
                .build();
    }

    private static Product getProduct1() {
        return Product.builder()
                .id(TEST_PRODUCT1_ID)
                .name(TEST_PRODUCT1_NAME)
                .price(TEST_PRODUCT1_PRICE)
                .isAvailable(true)
                .build();
    }
}