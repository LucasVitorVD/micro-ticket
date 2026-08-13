package com.ms.order_service.services;

import com.ms.order_service.TestcontainersConfiguration;
import com.ms.order_service.dtos.ShowReserveRequestDto;
import com.ms.order_service.exceptions.ResourceNotFoundException;
import com.ms.order_service.exceptions.TicketsUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockRestServiceServer
@Transactional
class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("should reserve tickets through catalog-service when show is available")
        void createOrderWhenShowAvailable() {
            UUID showId = UUID.randomUUID();

            String mockJsonResponse = """
                {
                    "id": "%s",
                    "name": "Luan Santana",
                    "description": "Um show muito animado e inédito!",
                    "price": 29.90,
                    "totalTickets": 100,
                    "availableTickets": 98
                }
            """.formatted(showId);

            mockServer.expect(requestTo("http://catalog-service/api/v1/show/" + showId + "/reserve"))
                    .andExpect(content().json("{\"quantity\": 2}"))
                    .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

            orderService.createOrder(showId, new ShowReserveRequestDto(2));

            mockServer.verify();
        }

        @Test
        @DisplayName("should throw TicketsUnavailableException when there are no tickets available")
        void throwErrorWhenTicketsNotAvailable() {
            UUID showId = UUID.randomUUID();

            String mockErrorResponse = """
                {
                    "message": "There aren't enough tickets."
                }
            """;

            mockServer.expect(requestTo("http://catalog-service/api/v1/show/" + showId + "/reserve"))
                    .andExpect(content().json("{\"quantity\": 2}"))
                    .andRespond(withStatus(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(mockErrorResponse));

            assertThrows(TicketsUnavailableException.class,
                    () -> orderService.createOrder(showId, new ShowReserveRequestDto(2)));

            mockServer.verify();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when show does not exist")
        void throwErrorWhenShowNotFound() {
            UUID showId = UUID.randomUUID();

            String mockErrorResponse = """
                {
                    "message": "Show not found!"
                }
            """;

            mockServer.expect(requestTo("http://catalog-service/api/v1/show/" + showId + "/reserve"))
                    .andExpect(content().json("{\"quantity\": 2}"))
                    .andRespond(withStatus(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(mockErrorResponse));

            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.createOrder(showId, new ShowReserveRequestDto(2)));

            mockServer.verify();
        }
    }
}
