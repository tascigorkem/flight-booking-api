package com.tascigorkem.flightbookingservice.service.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookingService {
    Page<BookingDto> getAllBookings(Pageable pageable);

    BookingDto getBookingById(UUID id);

    BookingDto addBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    BookingDto removeBooking(UUID id);
}
