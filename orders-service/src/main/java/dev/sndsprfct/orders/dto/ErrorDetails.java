package dev.sndsprfct.orders.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;

public record ErrorDetails(Instant timestamp, int status, String error) {
}
