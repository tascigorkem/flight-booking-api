package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlightMapper {

    FlightMapper FLIGHT_MAPPER = Mappers.getMapper( FlightMapper.class );

    FlightDto toFlightDto(FlightEntity flightEntity);

    List<FlightDto> toFlightDtoList(List<FlightEntity> flightEntityList);

    FlightEntity toFlightEntity(FlightDto flightDto);

    List<FlightEntity> toFlightEntityList(List<FlightDto> flightDtoList);
}
