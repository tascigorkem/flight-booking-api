package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AirportMapper {

    AirportMapper AIRPORT_MAPPER = Mappers.getMapper( AirportMapper.class );

    AirportDto toAirportDto(AirportEntity airportEntity);

    List<AirportDto> toAirportDtoList(List<AirportEntity> airportEntityList);

    AirportEntity toAirportEntity(AirportDto airportDto);

    List<AirportEntity> toAirportEntityList(List<AirportDto> airportDtoList);
}
