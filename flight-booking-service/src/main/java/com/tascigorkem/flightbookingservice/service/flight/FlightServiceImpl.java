package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.FlightNotFoundException;
import com.tascigorkem.flightbookingservice.repository.flight.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.FlightMapper.FLIGHT_MAPPER;


@RequiredArgsConstructor
@Transactional
@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Page<FlightDto> getAllFlights(Pageable pageable) {
        return flightRepository.findAllByDeletionTimeIsNull(pageable).map(FLIGHT_MAPPER::toFlightDto);
    }

    @Override
    public FlightDto getFlightById(UUID id) {
        return FLIGHT_MAPPER.toFlightDto(flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("id", id.toString())));
    }

    @Override
    public FlightDto addFlight(FlightDto flightDto) {
        FlightEntity flightEntity = FLIGHT_MAPPER.toFlightEntity(flightDto);
        flightEntity.setId(UUID.randomUUID());
        return FLIGHT_MAPPER.toFlightDto(flightRepository.save(flightEntity));
    }

    @Override
    public FlightDto updateFlight(FlightDto flightDto) {
        FlightEntity flightEntity = flightRepository.findById(flightDto.getId())
                .orElseThrow(() -> new FlightNotFoundException("id", flightDto.getId().toString()));

        flightEntity.setDepartureDate(flightDto.getDepartureDate());
        flightEntity.setArrivalDate(flightDto.getArrivalDate());
        flightEntity.setPrice(flightDto.getPrice());

        return FLIGHT_MAPPER.toFlightDto(flightRepository.save(flightEntity));
    }

    @Override
    public FlightDto removeFlight(UUID id) {
        FlightEntity flightEntity = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("id", id.toString()));

        flightEntity.setDeletionTime(LocalDateTime.now());

        return FLIGHT_MAPPER.toFlightDto(flightRepository.save(flightEntity));
    }
}
