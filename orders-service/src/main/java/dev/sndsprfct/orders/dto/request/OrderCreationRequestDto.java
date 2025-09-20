package dev.sndsprfct.orders.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record OrderCreationRequestDto(
        @NotNull
        Long customerId,// just for simplicity to avoid auth configuration
        @NotNull
        UUID idempotencyKey,
        @NotEmpty
        Map<Long, Integer> productsAmountByProductId,
        @NotNull
        String deliveryAddress) {
}
