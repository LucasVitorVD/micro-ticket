package com.ms.catalog_service.controllers;

import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.services.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
