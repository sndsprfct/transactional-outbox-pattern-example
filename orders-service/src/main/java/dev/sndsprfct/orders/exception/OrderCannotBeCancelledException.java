package dev.sndsprfct.orders.exception;

import dev.sndsprfct.orders.constant.OrderStatus;

public class OrderCannotBeCancelledException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Order '%d' has status '%s' and cannot be cancelled";

    public OrderCannotBeCancelledException(Long orderId, OrderStatus orderStatus) {
        super(EXCEPTION_MESSAGE_TEMPLATE.formatted(orderId, orderStatus));
    }
}
