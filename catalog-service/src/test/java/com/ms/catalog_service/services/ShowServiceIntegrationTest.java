package com.ms.catalog_service.services;

import com.ms.catalog_service.TestcontainersConfiguration;
import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.exceptions.InsufficientStockException;
import com.ms.catalog_service.exceptions.ResourceNotFoundException;
import com.ms.catalog_service.repository.ShowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class ShowServiceIntegrationTest {

    @Autowired
    private ShowService showService;

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
    @DisplayName("getShows")
    class GetShows {

        @Test
        @DisplayName("returns every persisted show")
        void returnsAllPersistedShows() {
            Show persisted = persistShow(100, 100);

            List<ShowResponseDto> result = showService.getShows();

            assertThat(result).hasSize(1);
            ShowResponseDto dto = result.getFirst();
            assertThat(dto.id()).isEqualTo(persisted.getId());
            assertThat(dto.name()).isEqualTo(persisted.getName());
            assertThat(dto.availableTickets()).isEqualTo(100);
        }

        @Test
        @DisplayName("returns an empty list when there are no shows")
        void returnsEmptyListWhenThereAreNoShows() {
            List<ShowResponseDto> result = showService.getShows();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getShow")
    class GetShow {

        @Test
        @DisplayName("returns the show when the id exists")
        void returnsShowWhenIdExists() {
            Show persisted = persistShow(100, 100);

            var result = showService.getShow(persisted.getId());

            assertThat(result.id()).isEqualTo(persisted.getId());
            assertThat(result.name()).isEqualTo(persisted.getName());
            assertThat(result.description()).isEqualTo(persisted.getDescription());
            assertThat(result.availableTickets()).isEqualTo(100);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the id does not exist")
        void throwsWhenIdDoesNotExist() {
            UUID invalidShowId = UUID.randomUUID();

            assertThatThrownBy(() -> showService.getShow(invalidShowId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Show not found!");
        }
    }

    @Nested
    @DisplayName("reserveShowTickets")
    class ReserveShowTickets {

        @Test
        @DisplayName("decrements available tickets when there's enough stock")
        void reservesTicketsWhenStockIsSufficient() {
            Show persisted = persistShow(100, 100);

            var result = showService.reserveShowTickets(persisted.getId(), 2);

            assertThat(result.availableTickets()).isEqualTo(98);
        }

        @Test
        @DisplayName("allows reserving exactly the remaining stock")
        void reservesExactlyTheRemainingStock() {
            Show persisted = persistShow(100, 3);

            var result = showService.reserveShowTickets(persisted.getId(), 3);

            assertThat(result.availableTickets()).isZero();
        }

        @Test
        @DisplayName("throws InsufficientStockException when quantity exceeds available stock")
        void throwsWhenStockIsInsufficient() {
            Show persisted = persistShow(100, 100);

            assertThatThrownBy(() -> showService.reserveShowTickets(persisted.getId(), 101))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessage("There aren't enough tickets.");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the show does not exist")
        void throwsWhenShowDoesNotExist() {
            UUID invalidShowId = UUID.randomUUID();

            assertThatThrownBy(() -> showService.reserveShowTickets(invalidShowId, 1))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Show not found!");
        }

        // This test needs committed data visible to other DB connections while
        // it runs, so it opts out of the class-level @Transactional rollback
        // (worker threads don't share the main thread's uncommitted transaction
        // anyway) and cleans up manually at the end instead.
        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        @DisplayName("never oversells tickets when reserved concurrently")
        void doesNotOversellUnderConcurrency() throws InterruptedException {
            Show persisted = persistShow(10, 10);
            int attempts = 20;

            ExecutorService executor = Executors.newFixedThreadPool(attempts);
            CountDownLatch ready = new CountDownLatch(attempts);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(attempts);
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failureCount = new AtomicInteger();

            try {
                for (int i = 0; i < attempts; i++) {
                    executor.submit(() -> {
                        ready.countDown();
                        try {
                            start.await();
                            showService.reserveShowTickets(persisted.getId(), 1);
                            successCount.incrementAndGet();
                        } catch (InsufficientStockException e) {
                            failureCount.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            done.countDown();
                        }
                    });
                }

                ready.await();
                start.countDown();
                done.await(30, TimeUnit.SECONDS);
            } finally {
                executor.shutdown();
            }

            assertThat(successCount.get()).isEqualTo(10);
            assertThat(failureCount.get()).isEqualTo(10);

            Show updated = showRepository.findById(persisted.getId()).orElseThrow();
            assertThat(updated.getAvailableTickets()).isZero();

            showRepository.delete(updated);
        }
    }
}
