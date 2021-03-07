package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
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
class FlightRepositoryIT {

    @Autowired
    private FlightRepository flightRepository;

    @Test
    void testFindAllByDeletionTimeIsNull() {
        // arrange
        UUID fakeFlightId1 = EntityModelFaker.fakeId();
        UUID fakeFlightId2 = EntityModelFaker.fakeId();

        FlightEntity fakeFlightEntity1 = EntityModelFaker.getFakeFlightEntity(fakeFlightId1, false);
        FlightEntity fakeFlightEntity2 = EntityModelFaker.getFakeFlightEntity(fakeFlightId2, false);

        fakeFlightEntity2.setDeletionTime(LocalDateTime.now());

        List<FlightEntity> flightEntities = Arrays.asList(fakeFlightEntity1, fakeFlightEntity2);

        // prepare db; delete all elements and insert entities
        flightRepository.deleteAll();
        flightRepository.saveAll(flightEntities);

        // act
        Pageable pageable = PageRequest.of(0,50);
        Page<FlightEntity> resultFlightsEntitiesPage = flightRepository.findAllByDeletionTimeIsNull(pageable);

        // assert
        assertNotNull(resultFlightsEntitiesPage.getContent());
        List<FlightEntity> resultFlightsEntities = resultFlightsEntitiesPage.getContent();

        Optional<FlightEntity> optFlightEntity1 = resultFlightsEntities.stream().filter(flightEntity -> flightEntity.getId().equals(fakeFlightId1)).findAny();
        Optional<FlightEntity> optFlightEntity2 = resultFlightsEntities.stream().filter(flightEntity -> flightEntity.getId().equals(fakeFlightId2)).findAny();

        assertTrue(optFlightEntity1.isPresent());
        assertFalse(optFlightEntity2.isPresent());

        FlightEntity resultFlightEntity1 = optFlightEntity1.get();

        assertAll(
                () -> assertEquals(fakeFlightEntity1.getId(), resultFlightEntity1.getId()),
                () -> assertEquals(fakeFlightEntity1.getDepartureDate(), resultFlightEntity1.getDepartureDate()),
                () -> assertEquals(fakeFlightEntity1.getArrivalDate(), resultFlightEntity1.getArrivalDate()),
                () -> assertEquals(fakeFlightEntity1.getPrice(), resultFlightEntity1.getPrice()),

                () -> assertNotNull(resultFlightEntity1.getCreationTime()),
                () -> assertNotNull(resultFlightEntity1.getUpdateTime()),
                () -> assertNull(resultFlightEntity1.getDeletionTime())
        );
    }
}