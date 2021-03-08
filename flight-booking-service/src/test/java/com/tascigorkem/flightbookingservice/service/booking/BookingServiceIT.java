package com.tascigorkem.flightbookingservice.service.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.booking.BookingMapper.BOOKING_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceIT {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Autowired
    BookingServiceIT(BookingRepository bookingRepository, BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    /**
     * Integration test for BookingService:getAllBookings
     * Checking whether connection with BookingRepository is successful
     */
    @Test
    void testGetAllBookings() {
        // arrange
        UUID fakeBookingId1 = EntityModelFaker.fakeId();
        UUID fakeBookingId2 = EntityModelFaker.fakeId();

        List<BookingEntity> fakeBookingEntities = new ArrayList<>();
        fakeBookingEntities.add(EntityModelFaker.getFakeBookingEntity(fakeBookingId1, true));
        fakeBookingEntities.add(EntityModelFaker.getFakeBookingEntity(fakeBookingId2, true));

        List<BookingDto> expectedBookingDtos = BOOKING_MAPPER.toBookingDtoList(fakeBookingEntities);
        BookingDto expectedBookingDto1 = expectedBookingDtos.get(0);
        BookingDto expectedBookingDto2 = expectedBookingDtos.get(1);

        // prepare db, insert entities
        bookingRepository.deleteAll();
        bookingRepository.saveAll(fakeBookingEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // act

        Page<BookingDto> resultBookingDtoPage = bookingService.getAllBookings(pageable);

        // assert
        assertNotNull(resultBookingDtoPage.getContent());
        List<BookingDto> resultBookingDtoList = resultBookingDtoPage.getContent();

        Optional<BookingDto> optionalResultBookingDto1 = resultBookingDtoList.stream()
                .filter(bookingDtoItem -> fakeBookingId1.equals(bookingDtoItem.getId())).findAny();

        Optional<BookingDto> optionalResultBookingDto2 = resultBookingDtoList.stream()
                .filter(bookingDtoItem -> fakeBookingId2.equals(bookingDtoItem.getId())).findAny();

        assertTrue(optionalResultBookingDto1.isPresent());
        assertTrue(optionalResultBookingDto2.isPresent());

        BookingDto resultBookingDto1 = optionalResultBookingDto1.get();
        BookingDto resultBookingDto2 = optionalResultBookingDto2.get();

        assertAll(
                () -> assertEquals(expectedBookingDto1.getId(), resultBookingDto1.getId()),
                () -> assertNotNull(resultBookingDto1.getCreationTime()),
                () -> assertNotNull(resultBookingDto1.getUpdateTime()),

                () -> assertEquals(expectedBookingDto2.getId(), resultBookingDto2.getId()),
                () -> assertNotNull(resultBookingDto2.getCreationTime()),
                () -> assertNotNull(resultBookingDto2.getUpdateTime())
        );
    }
}
