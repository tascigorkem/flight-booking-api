package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AirlineMapper {

    AirlineMapper AIRLINE_MAPPER = Mappers.getMapper( AirlineMapper.class );

    AirlineDto toAirlineDto(AirlineEntity airlineEntity);

    List<AirlineDto> toAirlineDtoList(List<AirlineEntity> airlineEntityList);

    AirlineEntity toAirlineEntity(AirlineDto airlineDto);

    List<AirlineEntity> toAirlineEntityList(List<AirlineDto> airlineDtoList);
}
