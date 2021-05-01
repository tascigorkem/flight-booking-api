package com.tascigorkem.flightbookingservice.service.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.booking.BookingMapper.BOOKING_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final BookingService subject = new BookingServiceImpl(bookingRepository);

    /**
     * Unit test for BookingService:getAllBookings
     */
    @Test
    void getAllBookings_RetrieveBookings_ShouldReturnNotDeletedBookings() {
        // GIVEN
        List<BookingEntity> fakeBookingEntityList = Arrays.asList(
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true)
        );

        List<BookingDto> fakeBookingDtoList = BOOKING_MAPPER.toBookingDtoList(fakeBookingEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<BookingEntity> fakePageBookingEntity = new PageImpl<>(fakeBookingEntityList, pageable, fakeBookingEntityList.size());

        when(bookingRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageBookingEntity);

        // WHEN
        Page<BookingDto> result = subject.getAllBookings(pageable);

        // THEN
        assertEquals(fakeBookingDtoList, result.toList());
        verify(bookingRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for BookingService:getBookingById
     */
    @Test
    void getBookingById_WithBookingId_ShouldReturnBooking() {
        // GIVEN
        UUID fakeBookingId = EntityModelFaker.fakeId();
        BookingEntity fakeBookingEntity = EntityModelFaker.getFakeBookingEntity(fakeBookingId, true);
        BookingDto expectedBookingDto = BOOKING_MAPPER.toBookingDto(fakeBookingEntity);

        when(bookingRepository.findById(fakeBookingId)).thenReturn(Optional.of(fakeBookingEntity));

        // WHEN
        BookingDto result = subject.getBookingById(fakeBookingId);

        // THEN
        assertEquals(expectedBookingDto, result);
        verify(bookingRepository).findById(fakeBookingId);
    }

    /**
     * Unit test for BookingService:addBooking
     */
    @Test
    void addBooking_SaveIntoDatabase_ShouldSaveAndReturnSavedBooking() {
        // GIVEN
        UUID fakeBookingId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingId, true);
        BookingEntity fakeBookingEntity = BOOKING_MAPPER.toBookingEntity(fakeBookingDto);
        BookingDto expectedBookingDto = BOOKING_MAPPER.toBookingDto(fakeBookingEntity);

        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(fakeBookingEntity);

        // WHEN
        BookingDto result = subject.addBooking(fakeBookingDto);

        // THEN
        assertEquals(expectedBookingDto, result);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    /**
     * Unit test for BookingService:updateBooking
     */
    @Test
    void updateBooking_SaveIntoDatabase_ShouldSaveAndReturnSavedBooking() {
        // GIVEN
        UUID fakeBookingId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingId, true);
        BookingEntity fakeBookingEntity = BOOKING_MAPPER.toBookingEntity(fakeBookingDto);
        BookingDto expectedBookingDto = BOOKING_MAPPER.toBookingDto(fakeBookingEntity);

        when(bookingRepository.findById(fakeBookingId)).thenReturn(Optional.of(fakeBookingEntity));
        when(bookingRepository.save(fakeBookingEntity)).thenReturn(fakeBookingEntity);

        // WHEN
        BookingDto result = subject.updateBooking(fakeBookingDto);

        // THEN
        assertEquals(expectedBookingDto, result);
        verify(bookingRepository).findById(fakeBookingId);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

    /**
     * Unit test for BookingService:removeBooking
     */
    @Test
    void removeBooking_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedBooking() {
        // GIVEN
        UUID fakeBookingId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingId, true);
        BookingEntity fakeBookingEntity = BOOKING_MAPPER.toBookingEntity(fakeBookingDto);
        BookingDto expectedBookingDto = BOOKING_MAPPER.toBookingDto(fakeBookingEntity);

        when(bookingRepository.findById(fakeBookingId)).thenReturn(Optional.of(fakeBookingEntity));
        when(bookingRepository.save(fakeBookingEntity)).thenReturn(fakeBookingEntity);

        // WHEN
        BookingDto result = subject.removeBooking(fakeBookingId);

        // THEN
        assertEquals(expectedBookingDto, result);
        verify(bookingRepository).findById(fakeBookingId);
        verify(bookingRepository).save(any(BookingEntity.class));
    }

}
