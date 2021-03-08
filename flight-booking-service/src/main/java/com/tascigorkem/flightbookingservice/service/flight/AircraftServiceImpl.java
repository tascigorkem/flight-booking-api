package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.AircraftNotFoundException;
import com.tascigorkem.flightbookingservice.repository.flight.AircraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AircraftMapper.AIRCRAFT_MAPPER;


@RequiredArgsConstructor
@Service
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;

    @Override
    public Page<AircraftDto> getAllAircrafts(Pageable pageable) {
        return aircraftRepository.findAllByDeletionTimeIsNull(pageable).map(AIRCRAFT_MAPPER::toAircraftDto);
    }

    @Override
    public AircraftDto getAircraftById(UUID id) {
        return AIRCRAFT_MAPPER.toAircraftDto(aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("id", id.toString())));
    }

    @Override
    public AircraftDto addAircraft(AircraftDto aircraftDto) {
        AircraftEntity aircraftEntity = AIRCRAFT_MAPPER.toAircraftEntity(aircraftDto);
        aircraftEntity.setId(UUID.randomUUID());
        return AIRCRAFT_MAPPER.toAircraftDto(aircraftRepository.save(aircraftEntity));
    }

    @Override
    public AircraftDto updateAircraft(AircraftDto aircraftDto) {
        AircraftEntity aircraftEntity = aircraftRepository.findById(aircraftDto.getId())
                .orElseThrow(() -> new AircraftNotFoundException("id", aircraftDto.getId().toString()));

        aircraftEntity.setModelName(aircraftDto.getModelName());
        aircraftEntity.setCode(aircraftDto.getCode());
        aircraftEntity.setSeat(aircraftDto.getSeat());
        aircraftEntity.setCountry(aircraftDto.getCountry());

        return AIRCRAFT_MAPPER.toAircraftDto(aircraftRepository.save(aircraftEntity));
    }

    @Override
    public AircraftDto removeAircraft(UUID id) {
        AircraftEntity aircraftEntity = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException("id", id.toString()));

        aircraftEntity.setDeletionTime(LocalDateTime.now());

        return AIRCRAFT_MAPPER.toAircraftDto(aircraftRepository.save(aircraftEntity));
    }
}
