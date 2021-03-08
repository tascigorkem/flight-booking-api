package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirlineRepository;
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

import static com.tascigorkem.flightbookingservice.service.flight.AirlineMapper.AIRLINE_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AirlineServiceIT {

    private final AirlineRepository airlineRepository;
    private final AirlineService airlineService;

    @Autowired
    AirlineServiceIT(AirlineRepository airlineRepository, AirlineService airlineService) {
        this.airlineRepository = airlineRepository;
        this.airlineService = airlineService;
    }

    /**
     * Integration test for AirlineService:getAllAirlines
     * Checking whether connection with AirlineRepository is successful
     */
    @Test
    void testGetAllAirlines() {
        // arrange
        UUID fakeAirlineId1 = EntityModelFaker.fakeId();
        UUID fakeAirlineId2 = EntityModelFaker.fakeId();

        List<AirlineEntity> fakeAirlineEntities = new ArrayList<>();
        fakeAirlineEntities.add(EntityModelFaker.getFakeAirlineEntity(fakeAirlineId1, true));
        fakeAirlineEntities.add(EntityModelFaker.getFakeAirlineEntity(fakeAirlineId2, true));

        List<AirlineDto> expectedAirlineDtos = AIRLINE_MAPPER.toAirlineDtoList(fakeAirlineEntities);
        AirlineDto expectedAirlineDto1 = expectedAirlineDtos.get(0);
        AirlineDto expectedAirlineDto2 = expectedAirlineDtos.get(1);

        // prepare db, insert entities
        airlineRepository.deleteAll();
        airlineRepository.saveAll(fakeAirlineEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // act

        Page<AirlineDto> resultAirlineDtoPage = airlineService.getAllAirlines(pageable);

        // assert
        assertNotNull(resultAirlineDtoPage.getContent());
        List<AirlineDto> resultAirlineDtoList = resultAirlineDtoPage.getContent();

        Optional<AirlineDto> optionalResultAirlineDto1 = resultAirlineDtoList.stream()
                .filter(airlineDtoItem -> fakeAirlineId1.equals(airlineDtoItem.getId())).findAny();

        Optional<AirlineDto> optionalResultAirlineDto2 = resultAirlineDtoList.stream()
                .filter(airlineDtoItem -> fakeAirlineId2.equals(airlineDtoItem.getId())).findAny();

        assertTrue(optionalResultAirlineDto1.isPresent());
        assertTrue(optionalResultAirlineDto2.isPresent());

        AirlineDto resultAirlineDto1 = optionalResultAirlineDto1.get();
        AirlineDto resultAirlineDto2 = optionalResultAirlineDto2.get();

        assertAll(
                () -> assertEquals(expectedAirlineDto1.getId(), resultAirlineDto1.getId()),
                () -> assertNotNull(resultAirlineDto1.getCreationTime()),
                () -> assertNotNull(resultAirlineDto1.getUpdateTime()),

                () -> assertEquals(expectedAirlineDto2.getId(), resultAirlineDto2.getId()),
                () -> assertNotNull(resultAirlineDto2.getCreationTime()),
                () -> assertNotNull(resultAirlineDto2.getUpdateTime())
        );
    }
}
