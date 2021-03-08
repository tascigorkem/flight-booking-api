package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.AirportNotFoundException;
import com.tascigorkem.flightbookingservice.repository.flight.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AirportMapper.AIRPORT_MAPPER;


@RequiredArgsConstructor
@Service
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;

    @Override
    public Page<AirportDto> getAllAirports(Pageable pageable) {
        return airportRepository.findAllByDeletionTimeIsNull(pageable).map(AIRPORT_MAPPER::toAirportDto);
    }

    @Override
    public AirportDto getAirportById(UUID id) {
        return AIRPORT_MAPPER.toAirportDto(airportRepository.findById(id)
                .orElseThrow(() -> new AirportNotFoundException("id", id.toString())));
    }

    @Override
    public AirportDto addAirport(AirportDto airportDto) {
        AirportEntity airportEntity = AIRPORT_MAPPER.toAirportEntity(airportDto);
        airportEntity.setId(UUID.randomUUID());
        return AIRPORT_MAPPER.toAirportDto(airportRepository.save(airportEntity));
    }

    @Override
    public AirportDto updateAirport(AirportDto airportDto) {
        AirportEntity airportEntity = airportRepository.findById(airportDto.getId())
                .orElseThrow(() -> new AirportNotFoundException("id", airportDto.getId().toString()));

        airportEntity.setName(airportDto.getName());
        airportEntity.setCode(airportDto.getCode());
        airportEntity.setCity(airportDto.getCity());

        return AIRPORT_MAPPER.toAirportDto(airportRepository.save(airportEntity));
    }

    @Override
    public AirportDto removeAirport(UUID id) {
        AirportEntity airportEntity = airportRepository.findById(id)
                .orElseThrow(() -> new AirportNotFoundException("id", id.toString()));

        airportEntity.setDeletionTime(LocalDateTime.now());

        return AIRPORT_MAPPER.toAirportDto(airportRepository.save(airportEntity));
    }
}
