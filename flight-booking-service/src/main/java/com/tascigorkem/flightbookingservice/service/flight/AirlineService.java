package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AirlineService {
    Page<AirlineDto> getAllAirlines(Pageable pageable);

    AirlineDto getAirlineById(UUID id);

    AirlineDto addAirline(AirlineDto airlineDto);

    AirlineDto updateAirline(AirlineDto airlineDto);

    AirlineDto removeAirline(UUID id);
}
