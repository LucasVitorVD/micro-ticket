package com.ms.catalog_service.services;

import com.ms.catalog_service.dtos.ShowResponseDto;
import com.ms.catalog_service.entities.Show;
import com.ms.catalog_service.exceptions.ResourceNotFoundException;
import com.ms.catalog_service.mappers.ShowMapper;
import com.ms.catalog_service.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowService {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowMapper mapper;

    public List<ShowResponseDto> getShows() {
        List<Show> shows = showRepository.findAll();

        return shows.stream().map(mapper::toDto).toList();
    }

    public ShowResponseDto getShow(UUID showId) {
        var show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found!"));

        return mapper.toDto(show);
    }
}