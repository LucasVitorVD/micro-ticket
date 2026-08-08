package com.ms.catalog_service.controllers;

import com.ms.catalog_service.TestcontainersConfiguration;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.repository.ShowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ShowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowRepository showRepository;

    private Show persistShow(int totalTickets, int availableTickets) {
        return showRepository.save(new Show(
                null,
                "Peça de Teatro Integration Test",
                "Show usado só para validar o fluxo de listagem",
                75.50,
                totalTickets,
                availableTickets
        ));
    }

    @Nested
    @DisplayName("GET /api/v1/show/all")
    class GetAllShows {

        @Test
        @DisplayName("returns every persisted show")
        void returnsPersistedShows() throws Exception {
            Show persisted = persistShow(100, 100);

            mockMvc.perform(get("/api/v1/show/all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.length()").value(1),
                            jsonPath("$[0].name").value(persisted.getName())
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/show/{id}")
    class GetShowById {

        @Test
        @DisplayName("returns the show when it exists")
        void returnsShowWhenItExists() throws Exception {
            Show persisted = persistShow(100, 100);

            mockMvc.perform(get("/api/v1/show/" + persisted.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.name").value(persisted.getName())
                    );
        }

        @Test
        @DisplayName("returns 404 when the show does not exist")
        void returns404WhenShowNotFound() throws Exception {
            UUID invalidShowId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/show/" + invalidShowId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").value(404),
                            jsonPath("$.message").value("Show not found!")
                    );
        }
    }

    @Nested
    @DisplayName("POST /api/v1/show/{id}/reserve")
    class ReserveTickets {

        @Test
        @DisplayName("reserves tickets and returns the updated show")
        void reservesTicketsWhenStockIsSufficient() throws Exception {
            Show persisted = persistShow(100, 100);

            mockMvc.perform(post("/api/v1/show/" + persisted.getId() + "/reserve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantity\": 2}"))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.availableTickets").value(98)
                    );
        }

        @Test
        @DisplayName("returns 409 when there isn't enough stock")
        void returns409WhenStockIsInsufficient() throws Exception {
            Show persisted = persistShow(100, 1);

            mockMvc.perform(post("/api/v1/show/" + persisted.getId() + "/reserve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantity\": 2}"))
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.message").value("There aren't enough tickets.")
                    );
        }

        @Test
        @DisplayName("returns 404 when the show does not exist")
        void returns404WhenShowNotFound() throws Exception {
            UUID invalidShowId = UUID.randomUUID();

            mockMvc.perform(post("/api/v1/show/" + invalidShowId + "/reserve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantity\": 1}"))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value("Show not found!")
                    );
        }

        @Test
        @DisplayName("returns 400 when quantity is not positive")
        void returns400WhenQuantityIsNotPositive() throws Exception {
            Show persisted = persistShow(100, 100);

            mockMvc.perform(post("/api/v1/show/" + persisted.getId() + "/reserve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"quantity\": -50}"))
                    .andExpectAll(
                            status().isBadRequest()
                    );
        }
    }
}
