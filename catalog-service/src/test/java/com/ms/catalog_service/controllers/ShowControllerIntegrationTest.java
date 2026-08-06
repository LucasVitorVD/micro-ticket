package com.ms.catalog_service.controllers;

import com.ms.catalog_service.TestcontainersConfiguration;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.repository.ShowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("Should get all shows")
    void shouldGetAllPersistedShows() throws Exception {
        Show persisted = showRepository.save(new Show(
                null,
                "Peça de Teatro Integration Test",
                "Show usado só para validar o fluxo de listagem",
                75.50,
                100,
                100
        ));

        mockMvc.perform(get("/api/v1/show/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(result ->
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].name").value(persisted.getName())
                );
    }
}
