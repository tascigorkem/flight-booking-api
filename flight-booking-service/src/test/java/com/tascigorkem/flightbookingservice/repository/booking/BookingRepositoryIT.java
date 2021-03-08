package com.tascigorkem.flightbookingservice.repository.booking;

import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
import com.tascigorkem.flightbookingservice.repository.flight.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingRepositoryIT {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final FlightRepository flightRepository;

    @Autowired
    BookingRepositoryIT(BookingRepository bookingRepository, CustomerRepository customerRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.flightRepository = flightRepository;
    }

    @Test
    void testFindAllByDeletionTimeIsNull() {
        // arrange
        UUID fakeBookingId1 = EntityModelFaker.fakeId();
        UUID fakeBookingId2 = EntityModelFaker.fakeId();

        BookingEntity fakeBookingEntity1 = EntityModelFaker.getFakeBookingEntity(fakeBookingId1, false);
        BookingEntity fakeBookingEntity2 = EntityModelFaker.getFakeBookingEntity(fakeBookingId2, false);

        fakeBookingEntity2.setDeletionTime(LocalDateTime.now());

        List<BookingEntity> bookingEntities = Arrays.asList(fakeBookingEntity1, fakeBookingEntity2);

        // prepare db; delete all elements and insert entities
        bookingRepository.deleteAll();
        bookingRepository.saveAll(bookingEntities);

        // act
        Pageable pageable = PageRequest.of(0,50);
        Page<BookingEntity> resultBookingsEntitiesPage = bookingRepository.findAllByDeletionTimeIsNull(pageable);

        // assert
        assertNotNull(resultBookingsEntitiesPage.getContent());
        List<BookingEntity> resultBookingsEntities = resultBookingsEntitiesPage.getContent();

        Optional<BookingEntity> optBookingEntity1 = resultBookingsEntities.stream().filter(bookingEntity -> bookingEntity.getId().equals(fakeBookingId1)).findAny();
        Optional<BookingEntity> optBookingEntity2 = resultBookingsEntities.stream().filter(bookingEntity -> bookingEntity.getId().equals(fakeBookingId2)).findAny();

        assertTrue(optBookingEntity1.isPresent());
        assertFalse(optBookingEntity2.isPresent());

        BookingEntity resultBookingEntity1 = optBookingEntity1.get();

        assertAll(
                () -> assertEquals(fakeBookingEntity1.getId(), resultBookingEntity1.getId()),
                () -> assertEquals(fakeBookingEntity1.getState(), resultBookingEntity1.getState()),
                () -> assertEquals(fakeBookingEntity1.getPaymentDate(), resultBookingEntity1.getPaymentDate()),
                () -> assertEquals(fakeBookingEntity1.getPaymentAmount(), resultBookingEntity1.getPaymentAmount()),
                () -> assertEquals(fakeBookingEntity1.isInsurance(), resultBookingEntity1.isInsurance()),
                () -> assertEquals(fakeBookingEntity1.getLuggage(), resultBookingEntity1.getLuggage()),

                () -> assertNotNull(resultBookingEntity1.getCreationTime()),
                () -> assertNotNull(resultBookingEntity1.getUpdateTime()),
                () -> assertNull(resultBookingEntity1.getDeletionTime())
        );
    }

    @Test
    void testBookingRelations() {
        // arrange
        UUID fakeBookingId = EntityModelFaker.fakeId();
        UUID fakeCustomerId = EntityModelFaker.fakeId();
        UUID fakeFlightId = EntityModelFaker.fakeId();

        BookingEntity fakeBookingEntity = EntityModelFaker.getFakeBookingEntity(fakeBookingId, false);
        CustomerEntity fakeCustomerEntity = EntityModelFaker.getFakeCustomerEntity(fakeCustomerId, false);
        FlightEntity fakeFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeFlightId, false);

        customerRepository.save(fakeCustomerEntity);
        flightRepository.save(fakeFlightEntity);

        fakeBookingEntity.setCustomer(fakeCustomerEntity);
        fakeBookingEntity.setFlight(fakeFlightEntity);
        bookingRepository.save(fakeBookingEntity);

        // act
        Optional<BookingEntity> resultOptBookingEntity = bookingRepository.findById(fakeBookingId);

        //assert
        assertTrue(resultOptBookingEntity.isPresent());

        BookingEntity resultBookingEntity = resultOptBookingEntity.get();

        assertAll(
                () -> assertNotNull(resultBookingEntity.getCustomer()),
                () -> assertNotNull(resultBookingEntity.getFlight()),

                () -> assertEquals(fakeCustomerEntity.getId(), resultBookingEntity.getCustomer().getId()),
                () -> assertEquals(fakeFlightEntity.getId(), resultBookingEntity.getFlight().getId())
        );

    }
}