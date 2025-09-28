package dev.sndsprfct.orders.service;

import dev.sndsprfct.orders.constant.OrderStatus;
import dev.sndsprfct.orders.dto.request.OrderCreationRequestDto;
import dev.sndsprfct.orders.dto.response.OrderCancelledResponseDto;
import dev.sndsprfct.orders.dto.response.OrderCreatedResponseDto;
import dev.sndsprfct.orders.dto.response.OrderResponseDto;
import dev.sndsprfct.orders.entity.orders.Order;
import dev.sndsprfct.orders.entity.orders.Product;
import dev.sndsprfct.orders.exception.CustomerDoesNotHaveOrderWithSuchId;
import dev.sndsprfct.orders.exception.OrderCannotBeCancelledException;
import dev.sndsprfct.orders.exception.OrderNotFoundException;
import dev.sndsprfct.orders.exception.OrderWithSuchIdempotencyKeyAlreadyExistsException;
import dev.sndsprfct.orders.mapper.OrderMapper;
import dev.sndsprfct.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static dev.sndsprfct.orders.security.PrincipalUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderProcessingService orderProcessingService;

    public OrderCreatedResponseDto createOrder(OrderCreationRequestDto orderCreationRequestDto) {
        List<Product> products = validateOrderCreationRequestDto(orderCreationRequestDto);
        Order order = orderMapper.map(orderCreationRequestDto, products);
        order.setCustomerId(getCurrentUserId());
        Long createdOrderId = orderProcessingService.processOrderCreation(order);
        return new OrderCreatedResponseDto(createdOrderId);
    }

    public List<OrderResponseDto> findOrders() {
        return findAllOrdersByCustomerId().stream()
                .map(orderMapper::map)
                .toList();
    }

    public OrderResponseDto findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (!Objects.equals(order.getCustomerId(), getCurrentUserId())) {
                        throw new CustomerDoesNotHaveOrderWithSuchId(orderId);
                    }
                    return orderMapper.map(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public OrderCancelledResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!Objects.equals(order.getCustomerId(), getCurrentUserId())) {
            throw new CustomerDoesNotHaveOrderWithSuchId(orderId);
        }
        validateOrderStatusIsCancellable(order);
        orderProcessingService.processOrderCancellation(order);
        return new OrderCancelledResponseDto(orderId);
    }

    private void validateOrderStatusIsCancellable(Order order) {
        if (!OrderStatus.CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new OrderCannotBeCancelledException(order.getId(), order.getStatus());
        }
    }

    private List<Product> validateOrderCreationRequestDto(OrderCreationRequestDto orderCreationRequestDto) {
        List<Product> products = productService.checkProductsAvailability(orderCreationRequestDto.productsAmountByProductId().keySet());
        validateOrderIdempotencyKey(orderCreationRequestDto);
        return products;
    }

    private void validateOrderIdempotencyKey(OrderCreationRequestDto orderCreationRequestDto) {
        List<Order> customerOrders = findAllOrdersByCustomerId();
        if (!customerOrders.stream().filter(o -> o.getIdempotencyKey().equals(orderCreationRequestDto.idempotencyKey())).toList().isEmpty()) {
            throw new OrderWithSuchIdempotencyKeyAlreadyExistsException(orderCreationRequestDto.idempotencyKey());
        }
    }

    private List<Order> findAllOrdersByCustomerId() {
        return orderRepository.findAllOrdersByCustomerId(getCurrentUserId());
    }
}
