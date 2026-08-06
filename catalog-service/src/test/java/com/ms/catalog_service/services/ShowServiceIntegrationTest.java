package com.ms.catalog_service.services;

import com.ms.catalog_service.TestcontainersConfiguration;
import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.repository.ShowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ShowServiceIntegrationTest {

    @Autowired
    private ShowService showService;

    @Autowired
    private ShowRepository showRepository;

    @Test
    @DisplayName("Should return all persistent shows")
    void shouldReturnAllPersistedShows() {
        // Arrange
        Show persisted = showRepository.save(new Show(
                null,
                "Peça de Teatro Integration Test",
                "Show usado só para validar o fluxo de listagem",
                75.50,
                100,
                100
        ));

        // Act
        List<ShowResponseDto> result = showService.getShows();

        // Assert
        assertThat(result).hasSize(1);

        ShowResponseDto dto = result.getFirst();
        assertThat(dto.id()).isEqualTo(persisted.getId());
        assertThat(dto.name()).isEqualTo("Peça de Teatro Integration Test");
        assertThat(dto.description()).isEqualTo("Show usado só para validar o fluxo de listagem");
        assertThat(dto.price()).isEqualTo(75.50);
        assertThat(dto.totalTickets()).isEqualTo(100);
        assertThat(dto.availableTickets()).isEqualTo(100);
    }
}
