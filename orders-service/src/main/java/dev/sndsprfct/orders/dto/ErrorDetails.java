package dev.sndsprfct.orders.dto;

import java.time.Instant;

public record ErrorDetails(Instant timestamp, int status, String error) {
}
