package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AirportRepositoryIT {

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    @Autowired
    AirportRepositoryIT(AirportRepository airportRepository, FlightRepository flightRepository) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
    }

    @Test
    void testFindAllByDeletionTimeIsNull() {
        // arrange
        UUID fakeAirportId1 = EntityModelFaker.fakeId();
        UUID fakeAirportId2 = EntityModelFaker.fakeId();

        AirportEntity fakeAirportEntity1 = EntityModelFaker.getFakeAirportEntity(fakeAirportId1, false);
        AirportEntity fakeAirportEntity2 = EntityModelFaker.getFakeAirportEntity(fakeAirportId2, false);

        fakeAirportEntity2.setDeletionTime(LocalDateTime.now());

        List<AirportEntity> airportEntities = Arrays.asList(fakeAirportEntity1, fakeAirportEntity2);

        // prepare db; delete all elements and insert entities
        airportRepository.deleteAll();
        airportRepository.saveAll(airportEntities);

        // act
        Pageable pageable = PageRequest.of(0,50);
        Page<AirportEntity> resultAirportsEntitiesPage = airportRepository.findAllByDeletionTimeIsNull(pageable);

        // assert
        assertNotNull(resultAirportsEntitiesPage.getContent());
        List<AirportEntity> resultAirportsEntities = resultAirportsEntitiesPage.getContent();

        Optional<AirportEntity> optAirportEntity1 = resultAirportsEntities.stream().filter(airportEntity -> airportEntity.getId().equals(fakeAirportId1)).findAny();
        Optional<AirportEntity> optAirportEntity2 = resultAirportsEntities.stream().filter(airportEntity -> airportEntity.getId().equals(fakeAirportId2)).findAny();

        assertTrue(optAirportEntity1.isPresent());
        assertFalse(optAirportEntity2.isPresent());

        AirportEntity resultAirportEntity1 = optAirportEntity1.get();

        assertAll(
                () -> assertEquals(fakeAirportEntity1.getId(), resultAirportEntity1.getId()),
                () -> assertEquals(fakeAirportEntity1.getCode(), resultAirportEntity1.getCode()),
                () -> assertEquals(fakeAirportEntity1.getCity(), resultAirportEntity1.getCity()),

                () -> assertNotNull(resultAirportEntity1.getCreationTime()),
                () -> assertNotNull(resultAirportEntity1.getUpdateTime()),
                () -> assertNull(resultAirportEntity1.getDeletionTime())
        );
    }

    @Test
    void testAirportRelations() {
        // arrange
        UUID fakeAirportId = EntityModelFaker.fakeId();
        UUID fakeDeptFlightId = EntityModelFaker.fakeId();
        UUID fakeDestFlightId = EntityModelFaker.fakeId();

        AirportEntity fakeAirportEntity = EntityModelFaker.getFakeAirportEntity(fakeAirportId, false);
        FlightEntity fakeDeptFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeDeptFlightId, false);
        FlightEntity fakeDestFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeDestFlightId, false);

        fakeAirportEntity.setDepartureFlights(Collections.singletonList(fakeDeptFlightEntity));
        fakeAirportEntity.setDestinationFlights(Collections.singletonList(fakeDestFlightEntity));
        airportRepository.save(fakeAirportEntity);

        fakeDeptFlightEntity.setDepartureAirport(fakeAirportEntity);
        flightRepository.save(fakeDeptFlightEntity);

        fakeDestFlightEntity.setDestinationAirport(fakeAirportEntity);
        flightRepository.save(fakeDestFlightEntity);

        // act
        Optional<AirportEntity> resultOptAirportEntity = airportRepository.findById(fakeAirportId);

        // assert
        assertTrue(resultOptAirportEntity.isPresent());
        AirportEntity resultAirportEntity = resultOptAirportEntity.get();

        assertAll(
                () -> assertNotNull(resultAirportEntity.getDestinationFlights()),
                () -> assertNotNull(resultAirportEntity.getDepartureFlights()),

                () -> assertEquals(1, resultAirportEntity.getDestinationFlights().size()),
                () -> assertEquals(1, resultAirportEntity.getDepartureFlights().size()),

                () -> assertEquals(fakeDestFlightEntity.getId(), resultAirportEntity.getDestinationFlights().get(0).getId()),
                () -> assertEquals(fakeDeptFlightEntity.getId(), resultAirportEntity.getDepartureFlights().get(0).getId())
        );
    }
}