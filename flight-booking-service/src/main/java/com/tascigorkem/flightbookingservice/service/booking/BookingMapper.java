package com.tascigorkem.flightbookingservice.service.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookingMapper {

    BookingMapper BOOKING_MAPPER = Mappers.getMapper( BookingMapper.class );

    BookingDto toBookingDto(BookingEntity bookingEntity);

    List<BookingDto> toBookingDtoList(List<BookingEntity> bookingEntityList);

    BookingEntity toBookingEntity(BookingDto bookingDto);

    List<BookingEntity> toBookingEntityList(List<BookingDto> bookingDtoList);
}
