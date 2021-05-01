package com.tascigorkem.flightbookingservice.service.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.BookingNotFoundException;
import com.tascigorkem.flightbookingservice.repository.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.booking.BookingMapper.BOOKING_MAPPER;


@RequiredArgsConstructor
@Transactional
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Page<BookingDto> getAllBookings(Pageable pageable) {
        return bookingRepository.findAllByDeletionTimeIsNull(pageable).map(BOOKING_MAPPER::toBookingDto);
    }

    @Override
    public BookingDto getBookingById(UUID id) {
        return BOOKING_MAPPER.toBookingDto(bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("id", id.toString())));
    }

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        BookingEntity bookingEntity = BOOKING_MAPPER.toBookingEntity(bookingDto);
        bookingEntity.setId(UUID.randomUUID());
        return BOOKING_MAPPER.toBookingDto(bookingRepository.save(bookingEntity));
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingDto.getId())
                .orElseThrow(() -> new BookingNotFoundException("id", bookingDto.getId().toString()));

        bookingEntity.setState(bookingDto.getState());
        bookingEntity.setPaymentDate(bookingDto.getPaymentDate());
        bookingEntity.setPaymentAmount(bookingDto.getPaymentAmount());
        bookingEntity.setInsurance(bookingDto.isInsurance());
        bookingEntity.setLuggage(bookingDto.getLuggage());

        return BOOKING_MAPPER.toBookingDto(bookingRepository.save(bookingEntity));
    }

    @Override
    public BookingDto removeBooking(UUID id) {
        BookingEntity bookingEntity = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("id", id.toString()));

        bookingEntity.setDeletionTime(LocalDateTime.now());

        return BOOKING_MAPPER.toBookingDto(bookingRepository.save(bookingEntity));
    }
}
