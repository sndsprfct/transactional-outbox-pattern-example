package dev.sndsprfct.orders.service;

import dev.sndsprfct.orders.dto.request.OrderCreationRequestDto;
import dev.sndsprfct.orders.dto.response.OrderCreatedResponseDto;
import dev.sndsprfct.orders.dto.response.OrderResponseDto;
import dev.sndsprfct.orders.entity.Order;
import dev.sndsprfct.orders.entity.Product;
import dev.sndsprfct.orders.exception.OrderWithSuchIdempotencyKeyAlreadyExistsException;
import dev.sndsprfct.orders.mapper.OrderMapper;
import dev.sndsprfct.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderCreatedResponseDto createOrder(OrderCreationRequestDto orderCreationRequestDto) {
        List<Product> products = productService.checkProductsAvailability(orderCreationRequestDto.productsAmountByProductId().keySet());
        validateOrderIdempotencyKey(orderCreationRequestDto);
        Order order = orderMapper.toEntity(orderCreationRequestDto, products);
        Order createdOrder = orderRepository.save(order);
        return new OrderCreatedResponseDto(createdOrder.getId());
    }

    public List<OrderResponseDto> findCustomerOrders(Long customerId) {
        return findAllOrdersByCustomerId(customerId).stream()
                .map(orderMapper::map)
                .toList();
    }

    private void validateOrderIdempotencyKey(OrderCreationRequestDto orderCreationRequestDto) {
        List<Order> customerOrders = findAllOrdersByCustomerId(orderCreationRequestDto.customerId());
        if (!customerOrders.stream().filter(o -> o.getIdempotencyKey().equals(orderCreationRequestDto.idempotencyKey())).toList().isEmpty()) {
            throw new OrderWithSuchIdempotencyKeyAlreadyExistsException(orderCreationRequestDto.idempotencyKey());
        }
    }

    private List<Order> findAllOrdersByCustomerId(Long customerId) {
        return orderRepository.findAllOrdersByCustomerId(customerId);
    }
}
