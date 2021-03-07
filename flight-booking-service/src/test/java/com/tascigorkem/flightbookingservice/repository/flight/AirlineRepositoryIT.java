package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
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
class AirlineRepositoryIT {

    @Autowired
    private AirlineRepository airlineRepository;

    @Test
    void testFindAllByDeletionTimeIsNull() {
        // arrange
        UUID fakeAirlineId1 = EntityModelFaker.fakeId();
        UUID fakeAirlineId2 = EntityModelFaker.fakeId();

        AirlineEntity fakeAirlineEntity1 = EntityModelFaker.getFakeAirlineEntity(fakeAirlineId1, false);
        AirlineEntity fakeAirlineEntity2 = EntityModelFaker.getFakeAirlineEntity(fakeAirlineId2, false);

        fakeAirlineEntity2.setDeletionTime(LocalDateTime.now());

        List<AirlineEntity> airlineEntities = Arrays.asList(fakeAirlineEntity1, fakeAirlineEntity2);

        // prepare db; delete all elements and insert entities
        airlineRepository.deleteAll();
        airlineRepository.saveAll(airlineEntities);

        // act
        Pageable pageable = PageRequest.of(0,50);
        Page<AirlineEntity> resultAirlinesEntitiesPage = airlineRepository.findAllByDeletionTimeIsNull(pageable);

        // assert
        assertNotNull(resultAirlinesEntitiesPage.getContent());
        List<AirlineEntity> resultAirlinesEntities = resultAirlinesEntitiesPage.getContent();

        Optional<AirlineEntity> optAirlineEntity1 = resultAirlinesEntities.stream().filter(airlineEntity -> airlineEntity.getId().equals(fakeAirlineId1)).findAny();
        Optional<AirlineEntity> optAirlineEntity2 = resultAirlinesEntities.stream().filter(airlineEntity -> airlineEntity.getId().equals(fakeAirlineId2)).findAny();

        assertTrue(optAirlineEntity1.isPresent());
        assertFalse(optAirlineEntity2.isPresent());

        AirlineEntity resultAirlineEntity1 = optAirlineEntity1.get();

        assertAll(
                () -> assertEquals(fakeAirlineEntity1.getId(), resultAirlineEntity1.getId()),
                () -> assertEquals(fakeAirlineEntity1.getName(), resultAirlineEntity1.getName()),
                () -> assertEquals(fakeAirlineEntity1.getCountry(), resultAirlineEntity1.getCountry()),

                () -> assertNotNull(resultAirlineEntity1.getCreationTime()),
                () -> assertNotNull(resultAirlineEntity1.getUpdateTime()),
                () -> assertNull(resultAirlineEntity1.getDeletionTime())
        );
    }
}