package dev.sndsprfct.orders.handler;

import dev.sndsprfct.orders.dto.ErrorDetails;
import dev.sndsprfct.orders.exception.OrderWithSuchIdempotencyKeyAlreadyExistsException;
import dev.sndsprfct.orders.exception.ProductsNotAvailableException;
import dev.sndsprfct.orders.exception.ProductsNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductsNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleProductsNotFoundException(ProductsNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDetails(Instant.now(), HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(ProductsNotAvailableException.class)
    public ResponseEntity<ErrorDetails> handleProductsNotAvailableException(ProductsNotAvailableException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDetails(Instant.now(), HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(OrderWithSuchIdempotencyKeyAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleOrderWithSuchIdempotencyKeyAlreadyExistsException(OrderWithSuchIdempotencyKeyAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorDetails(Instant.now(), HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
