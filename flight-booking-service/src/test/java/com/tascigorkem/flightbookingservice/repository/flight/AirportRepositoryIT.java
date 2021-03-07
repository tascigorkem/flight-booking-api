package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
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
class AirportRepositoryIT {

    @Autowired
    private AirportRepository airportRepository;

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
}