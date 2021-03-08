package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AirportService {
    Page<AirportDto> getAllAirports(Pageable pageable);

    AirportDto getAirportById(UUID id);

    AirportDto addAirport(AirportDto airportDto);

    AirportDto updateAirport(AirportDto airportDto);

    AirportDto removeAirport(UUID id);
}
