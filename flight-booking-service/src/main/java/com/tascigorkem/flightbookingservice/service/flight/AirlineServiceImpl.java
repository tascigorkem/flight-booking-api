package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.AirlineNotFoundException;
import com.tascigorkem.flightbookingservice.repository.flight.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AirlineMapper.AIRLINE_MAPPER;


@RequiredArgsConstructor
@Service
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    public Page<AirlineDto> getAllAirlines(Pageable pageable) {
        return airlineRepository.findAllByDeletionTimeIsNull(pageable).map(AIRLINE_MAPPER::toAirlineDto);
    }

    @Override
    public AirlineDto getAirlineById(UUID id) {
        return AIRLINE_MAPPER.toAirlineDto(airlineRepository.findById(id)
                .orElseThrow(() -> new AirlineNotFoundException("id", id.toString())));
    }

    @Override
    public AirlineDto addAirline(AirlineDto airlineDto) {
        AirlineEntity airlineEntity = AIRLINE_MAPPER.toAirlineEntity(airlineDto);
        airlineEntity.setId(UUID.randomUUID());
        return AIRLINE_MAPPER.toAirlineDto(airlineRepository.save(airlineEntity));
    }

    @Override
    public AirlineDto updateAirline(AirlineDto airlineDto) {
        AirlineEntity airlineEntity = airlineRepository.findById(airlineDto.getId())
                .orElseThrow(() -> new AirlineNotFoundException("id", airlineDto.getId().toString()));

        airlineEntity.setName(airlineDto.getName());
        airlineEntity.setCountry(airlineDto.getCountry());

        return AIRLINE_MAPPER.toAirlineDto(airlineRepository.save(airlineEntity));
    }

    @Override
    public AirlineDto removeAirline(UUID id) {
        AirlineEntity airlineEntity = airlineRepository.findById(id)
                .orElseThrow(() -> new AirlineNotFoundException("id", id.toString()));

        airlineEntity.setDeletionTime(LocalDateTime.now());

        return AIRLINE_MAPPER.toAirlineDto(airlineRepository.save(airlineEntity));
    }
}
