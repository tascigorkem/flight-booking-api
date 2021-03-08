package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FlightService {
    Page<FlightDto> getAllFlights(Pageable pageable);

    FlightDto getFlightById(UUID id);

    FlightDto addFlight(FlightDto flightDto);

    FlightDto updateFlight(FlightDto flightDto);

    FlightDto removeFlight(UUID id);
}
