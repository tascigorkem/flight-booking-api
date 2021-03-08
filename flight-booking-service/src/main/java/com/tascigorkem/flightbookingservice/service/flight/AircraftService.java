package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AircraftService {
    Page<AircraftDto> getAllAircrafts(Pageable pageable);

    AircraftDto getAircraftById(UUID id);

    AircraftDto addAircraft(AircraftDto aircraftDto);

    AircraftDto updateAircraft(AircraftDto aircraftDto);

    AircraftDto removeAircraft(UUID id);
}
