package com.ms.catalog_service.repository;

import com.ms.catalog_service.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Show s SET s.availableTickets = s.availableTickets - :quantity " +
            "WHERE s.id = :id AND s.availableTickets >= :quantity")
    int reserveTickets(@Param("id") UUID id, @Param("quantity") int quantity);
}
