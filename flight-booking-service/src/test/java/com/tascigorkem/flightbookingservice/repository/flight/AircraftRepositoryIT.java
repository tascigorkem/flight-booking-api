package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
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
class AircraftRepositoryIT {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;

    @Autowired
    AircraftRepositoryIT(AircraftRepository aircraftRepository, FlightRepository flightRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
    }

    @Test
    void findAllByDeletionTimeIsNull_RetrieveNotDeletedEntites_ShouldReturnNotDeletedEntites() {
        // GIVEN
        UUID fakeAircraftId1 = EntityModelFaker.fakeId();
        UUID fakeAircraftId2 = EntityModelFaker.fakeId();

        AircraftEntity fakeAircraftEntity1 = EntityModelFaker.getFakeAircraftEntity(fakeAircraftId1, false);
        AircraftEntity fakeAircraftEntity2 = EntityModelFaker.getFakeAircraftEntity(fakeAircraftId2, false);

        fakeAircraftEntity2.setDeletionTime(LocalDateTime.now());

        List<AircraftEntity> aircraftEntities = Arrays.asList(fakeAircraftEntity1, fakeAircraftEntity2);

        // prepare db; delete all elements and insert entities
        aircraftRepository.deleteAll();
        aircraftRepository.saveAll(aircraftEntities);

        // WHEN
        Pageable pageable = PageRequest.of(0, 50);
        Page<AircraftEntity> resultAircraftEntitiesPage = aircraftRepository.findAllByDeletionTimeIsNull(pageable);

        // THEN
        assertNotNull(resultAircraftEntitiesPage.getContent());
        List<AircraftEntity> resultAircraftEntities = resultAircraftEntitiesPage.getContent();

        Optional<AircraftEntity> optAircraftEntity1 = resultAircraftEntities.stream().filter(aircraftEntity -> aircraftEntity.getId().equals(fakeAircraftId1)).findAny();
        Optional<AircraftEntity> optAircraftEntity2 = resultAircraftEntities.stream().filter(aircraftEntity -> aircraftEntity.getId().equals(fakeAircraftId2)).findAny();

        assertTrue(optAircraftEntity1.isPresent());
        assertFalse(optAircraftEntity2.isPresent());

        AircraftEntity resultAircraftEntity1 = optAircraftEntity1.get();

        assertAll(
                () -> assertEquals(fakeAircraftEntity1.getId(), resultAircraftEntity1.getId()),
                () -> assertEquals(fakeAircraftEntity1.getModelName(), resultAircraftEntity1.getModelName()),
                () -> assertEquals(fakeAircraftEntity1.getCode(), resultAircraftEntity1.getCode()),
                () -> assertEquals(fakeAircraftEntity1.getSeat(), resultAircraftEntity1.getSeat()),
                () -> assertEquals(fakeAircraftEntity1.getCountry(), resultAircraftEntity1.getCountry()),
                () -> assertEquals(fakeAircraftEntity1.getManufacturerDate(), resultAircraftEntity1.getManufacturerDate()),

                () -> assertNotNull(resultAircraftEntity1.getCreationTime()),
                () -> assertNotNull(resultAircraftEntity1.getUpdateTime()),
                () -> assertNull(resultAircraftEntity1.getDeletionTime())
        );
    }

    @Test
    void testAircraftRelations() {
        // GIVEN
        UUID fakeAircraftId = EntityModelFaker.fakeId();
        UUID fakeFlightId = EntityModelFaker.fakeId();

        AircraftEntity fakeAircraftEntity = EntityModelFaker.getFakeAircraftEntity(fakeAircraftId, false);
        FlightEntity fakeFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeFlightId, false);

        // set entity relations
        fakeAircraftEntity.setFlights(Collections.singletonList(fakeFlightEntity));
        aircraftRepository.save(fakeAircraftEntity);

        fakeFlightEntity.setAircraft(fakeAircraftEntity);
        flightRepository.save(fakeFlightEntity);

        // WHEN
        Optional<AircraftEntity> resultOptAircraftEntity = aircraftRepository.findById(fakeAircraftId);

        // THEN
        assertTrue(resultOptAircraftEntity.isPresent());
        AircraftEntity resultAircraftEntity = resultOptAircraftEntity.get();

        assertAll(
                () -> assertNotNull(resultAircraftEntity.getFlights()),

                () -> assertEquals(1, resultAircraftEntity.getFlights().size()),

                () -> assertEquals(fakeFlightEntity.getId(), resultAircraftEntity.getFlights().get(0).getId())
        );
    }
}