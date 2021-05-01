package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AircraftRepository;
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

import static com.tascigorkem.flightbookingservice.service.flight.AircraftMapper.AIRCRAFT_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AircraftServiceIT {

    private final AircraftRepository aircraftRepository;
    private final AircraftService aircraftService;

    @Autowired
    AircraftServiceIT(AircraftRepository aircraftRepository, AircraftService aircraftService) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftService = aircraftService;
    }

    /**
     * Integration test for AircraftService:getAllAircrafts
     * Checking whether connection with AircraftRepository is successful
     */
    @Test
    void getAllAircrafts_RetrieveAircrafts_ShouldReturnNotDeletedAircrafts() {
        // GIVEN
        UUID fakeAircraftId1 = EntityModelFaker.fakeId();
        UUID fakeAircraftId2 = EntityModelFaker.fakeId();

        List<AircraftEntity> fakeAircraftEntities = new ArrayList<>();
        fakeAircraftEntities.add(EntityModelFaker.getFakeAircraftEntity(fakeAircraftId1, true));
        fakeAircraftEntities.add(EntityModelFaker.getFakeAircraftEntity(fakeAircraftId2, true));

        List<AircraftDto> expectedAircraftDtos = AIRCRAFT_MAPPER.toAircraftDtoList(fakeAircraftEntities);
        AircraftDto expectedAircraftDto1 = expectedAircraftDtos.get(0);
        AircraftDto expectedAircraftDto2 = expectedAircraftDtos.get(1);

        // prepare db, insert entities
        aircraftRepository.deleteAll();
        aircraftRepository.saveAll(fakeAircraftEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // WHEN

        Page<AircraftDto> resultAircraftDtoPage = aircraftService.getAllAircrafts(pageable);

        // THEN
        assertNotNull(resultAircraftDtoPage.getContent());
        List<AircraftDto> resultAircraftDtoList = resultAircraftDtoPage.getContent();

        Optional<AircraftDto> optionalResultAircraftDto1 = resultAircraftDtoList.stream()
                .filter(aircraftDtoItem -> fakeAircraftId1.equals(aircraftDtoItem.getId())).findAny();

        Optional<AircraftDto> optionalResultAircraftDto2 = resultAircraftDtoList.stream()
                .filter(aircraftDtoItem -> fakeAircraftId2.equals(aircraftDtoItem.getId())).findAny();

        assertTrue(optionalResultAircraftDto1.isPresent());
        assertTrue(optionalResultAircraftDto2.isPresent());

        AircraftDto resultAircraftDto1 = optionalResultAircraftDto1.get();
        AircraftDto resultAircraftDto2 = optionalResultAircraftDto2.get();

        assertAll(
                () -> assertEquals(expectedAircraftDto1.getId(), resultAircraftDto1.getId()),
                () -> assertNotNull(resultAircraftDto1.getCreationTime()),
                () -> assertNotNull(resultAircraftDto1.getUpdateTime()),

                () -> assertEquals(expectedAircraftDto2.getId(), resultAircraftDto2.getId()),
                () -> assertNotNull(resultAircraftDto2.getCreationTime()),
                () -> assertNotNull(resultAircraftDto2.getUpdateTime())
        );
    }
}
