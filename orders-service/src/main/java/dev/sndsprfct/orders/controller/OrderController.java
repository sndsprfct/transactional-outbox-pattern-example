package dev.sndsprfct.orders.controller;

import dev.sndsprfct.orders.dto.request.OrderCreationRequestDto;
import dev.sndsprfct.orders.dto.response.OrderCreatedResponseDto;
import dev.sndsprfct.orders.dto.response.OrderResponseDto;
import dev.sndsprfct.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderCreatedResponseDto> createOrder(@Valid @RequestBody OrderCreationRequestDto orderCreationRequestDto) {
        OrderCreatedResponseDto createdOrder = orderService.createOrder(orderCreationRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam Long customerId) {
        List<OrderResponseDto> customerOrders = orderService.findCustomerOrders(customerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerOrders);
    }
}
