package dev.sndsprfct.orders.exception;

public class OutboxEventPayloadWasNotSerializedException extends RuntimeException {
    public OutboxEventPayloadWasNotSerializedException(Exception e) {
        super(e);
    }
}
