package dev.sndsprfct.orders.dto.response;

public record OrderItemResponseDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        Long unitPrice) {
}
