package com.ms.catalog_service.controllers;

import com.ms.catalog_service.dtos.ShowReserveRequestDto;
import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.services.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/show")
@RequiredArgsConstructor
public class ShowController {
    @Autowired
    private ShowService showService;

    @GetMapping("/all")
    public ResponseEntity<List<ShowResponseDto>> getAllShows() {
        return ResponseEntity.ok(showService.getShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponseDto> getShow(@PathVariable("id") UUID showId) {
        return ResponseEntity.ok(showService.getShow(showId));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<ShowResponseDto> reserveTickets(@PathVariable("id") UUID showId, @RequestBody @Valid ShowReserveRequestDto showReserveRequestDto) {
        var result = showService.reserveShowTickets(showId, showReserveRequestDto.quantity());
        return ResponseEntity.ok(result);
    }
}
