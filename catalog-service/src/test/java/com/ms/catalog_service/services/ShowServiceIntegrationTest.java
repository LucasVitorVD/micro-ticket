package com.ms.catalog_service.services;

import com.ms.catalog_service.TestcontainersConfiguration;
import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.exceptions.ResourceNotFoundException;
import com.ms.catalog_service.repository.ShowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("Should return show with valid id")
    void shouldReturnShowById() {
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
        var result = showService.getShow(persisted.getId());

        // Assert
        assertThat(result.id()).isEqualTo(persisted.getId());
        assertThat(result.name()).isEqualTo("Peça de Teatro Integration Test");
        assertThat(result.description()).isEqualTo("Show usado só para validar o fluxo de listagem");
        assertThat(result.price()).isEqualTo(75.50);
        assertThat(result.totalTickets()).isEqualTo(100);
        assertThat(result.availableTickets()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should throw a resource not found exception if show does not exists")
    void shouldThrowResourceNotFoundException() {
        // Arrange
        UUID invalidShowId = UUID.randomUUID();

        // Act
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> showService.getShow(invalidShowId)
        );

        // Assert
        assertEquals("Show not found!", exception.getMessage());
    }
}
