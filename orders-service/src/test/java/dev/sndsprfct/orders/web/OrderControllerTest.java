package dev.sndsprfct.orders.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sndsprfct.orders.controller.OrderController;
import dev.sndsprfct.orders.dto.ErrorDetails;
import dev.sndsprfct.orders.dto.request.OrderCreationRequestDto;
import dev.sndsprfct.orders.exception.ProductsNotAvailableException;
import dev.sndsprfct.orders.exception.ProductsNotFoundException;
import dev.sndsprfct.orders.service.OrderService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void testOrderCreationWhenProductsNotFound() {
        // given
        Mockito.when(orderService.createOrder(any()))
                .thenThrow(new ProductsNotFoundException(List.of(1L)));
        String orderCreationRequestDtoJson = getOrderCreationRequestDtoJson();
        ErrorDetails expectedErrorResponse = new ErrorDetails(null, HttpStatus.CONFLICT.value(), "Products with ids [1] not found");

        // when then
        String responseJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderCreationRequestDtoJson))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ErrorDetails errorDetails = objectMapper.readValue(responseJson, ErrorDetails.class);
        assertThat(errorDetails)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .isEqualTo(expectedErrorResponse);
    }

    @Test
    @SneakyThrows
    void testOrderCreationWhenProductsNotAvailable() {
        // given
        Mockito.when(orderService.createOrder(any()))
                .thenThrow(new ProductsNotAvailableException(List.of(1L)));
        String orderCreationRequestDtoJson = getOrderCreationRequestDtoJson();
        ErrorDetails expectedErrorResponse = new ErrorDetails(
                null,
                HttpStatus.CONFLICT.value(),
                "Products with ids [1] are not available");

        // when then
        String responseJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderCreationRequestDtoJson))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ErrorDetails errorDetails = objectMapper.readValue(responseJson, ErrorDetails.class);
        assertThat(errorDetails)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .isEqualTo(expectedErrorResponse);
    }

    private String getOrderCreationRequestDtoJson() throws JsonProcessingException {
        OrderCreationRequestDto orderCreationRequestDto = new OrderCreationRequestDto(
                1L,
                UUID.randomUUID(),
                Map.of(1L, 2),
                "Delivery Address");
        return objectMapper.writeValueAsString(orderCreationRequestDto);
    }
}
