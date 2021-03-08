package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirportRepository;
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

import static com.tascigorkem.flightbookingservice.service.flight.AirportMapper.AIRPORT_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AirportServiceIT {

    private final AirportRepository airportRepository;
    private final AirportService airportService;

    @Autowired
    AirportServiceIT(AirportRepository airportRepository, AirportService airportService) {
        this.airportRepository = airportRepository;
        this.airportService = airportService;
    }

    /**
     * Integration test for AirportService:getAllAirports
     * Checking whether connection with AirportRepository is successful
     */
    @Test
    void testGetAllAirports() {
        // arrange
        UUID fakeAirportId1 = EntityModelFaker.fakeId();
        UUID fakeAirportId2 = EntityModelFaker.fakeId();

        List<AirportEntity> fakeAirportEntities = new ArrayList<>();
        fakeAirportEntities.add(EntityModelFaker.getFakeAirportEntity(fakeAirportId1, true));
        fakeAirportEntities.add(EntityModelFaker.getFakeAirportEntity(fakeAirportId2, true));

        List<AirportDto> expectedAirportDtos = AIRPORT_MAPPER.toAirportDtoList(fakeAirportEntities);
        AirportDto expectedAirportDto1 = expectedAirportDtos.get(0);
        AirportDto expectedAirportDto2 = expectedAirportDtos.get(1);

        // prepare db, insert entities
        airportRepository.deleteAll();
        airportRepository.saveAll(fakeAirportEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // act

        Page<AirportDto> resultAirportDtoPage = airportService.getAllAirports(pageable);

        // assert
        assertNotNull(resultAirportDtoPage.getContent());
        List<AirportDto> resultAirportDtoList = resultAirportDtoPage.getContent();

        Optional<AirportDto> optionalResultAirportDto1 = resultAirportDtoList.stream()
                .filter(airportDtoItem -> fakeAirportId1.equals(airportDtoItem.getId())).findAny();

        Optional<AirportDto> optionalResultAirportDto2 = resultAirportDtoList.stream()
                .filter(airportDtoItem -> fakeAirportId2.equals(airportDtoItem.getId())).findAny();

        assertTrue(optionalResultAirportDto1.isPresent());
        assertTrue(optionalResultAirportDto2.isPresent());

        AirportDto resultAirportDto1 = optionalResultAirportDto1.get();
        AirportDto resultAirportDto2 = optionalResultAirportDto2.get();

        assertAll(
                () -> assertEquals(expectedAirportDto1.getId(), resultAirportDto1.getId()),
                () -> assertNotNull(resultAirportDto1.getCreationTime()),
                () -> assertNotNull(resultAirportDto1.getUpdateTime()),

                () -> assertEquals(expectedAirportDto2.getId(), resultAirportDto2.getId()),
                () -> assertNotNull(resultAirportDto2.getCreationTime()),
                () -> assertNotNull(resultAirportDto2.getUpdateTime())
        );
    }
}
