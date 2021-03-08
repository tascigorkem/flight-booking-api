package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AircraftMapper {

    AircraftMapper AIRCRAFT_MAPPER = Mappers.getMapper( AircraftMapper.class );

    AircraftDto toAircraftDto(AircraftEntity aircraftEntity);

    List<AircraftDto> toAircraftDtoList(List<AircraftEntity> aircraftEntityList);

    AircraftEntity toAircraftEntity(AircraftDto aircraftDto);

    List<AircraftEntity> toAircraftEntityList(List<AircraftDto> aircraftDtoList);
}
