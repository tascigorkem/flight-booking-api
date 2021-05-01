package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.FlightMapper.FLIGHT_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class FlightServiceIT {

    private final FlightRepository flightRepository;
    private final FlightService flightService;

    @Autowired
    FlightServiceIT(FlightRepository flightRepository, FlightService flightService) {
        this.flightRepository = flightRepository;
        this.flightService = flightService;
    }

    /**
     * Integration test for FlightService:getAllFlights
     * Checking whether connection with FlightRepository is successful
     */
    @Test
    void getAllFlights_RetrieveFlights_ShouldReturnNotDeletedFlights() {
        // GIVEN
        UUID fakeFlightId1 = EntityModelFaker.fakeId();
        UUID fakeFlightId2 = EntityModelFaker.fakeId();

        List<FlightEntity> fakeFlightEntities = new ArrayList<>();
        fakeFlightEntities.add(EntityModelFaker.getFakeFlightEntity(fakeFlightId1, true));
        fakeFlightEntities.add(EntityModelFaker.getFakeFlightEntity(fakeFlightId2, true));

        List<FlightDto> expectedFlightDtos = FLIGHT_MAPPER.toFlightDtoList(fakeFlightEntities);
        FlightDto expectedFlightDto1 = expectedFlightDtos.get(0);
        FlightDto expectedFlightDto2 = expectedFlightDtos.get(1);

        // prepare db, insert entities
        flightRepository.deleteAll();
        flightRepository.saveAll(fakeFlightEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // WHEN

        Page<FlightDto> resultFlightDtoPage = flightService.getAllFlights(pageable);

        // THEN
        assertNotNull(resultFlightDtoPage.getContent());
        List<FlightDto> resultFlightDtoList = resultFlightDtoPage.getContent();

        Optional<FlightDto> optionalResultFlightDto1 = resultFlightDtoList.stream()
                .filter(flightDtoItem -> fakeFlightId1.equals(flightDtoItem.getId())).findAny();

        Optional<FlightDto> optionalResultFlightDto2 = resultFlightDtoList.stream()
                .filter(flightDtoItem -> fakeFlightId2.equals(flightDtoItem.getId())).findAny();

        assertTrue(optionalResultFlightDto1.isPresent());
        assertTrue(optionalResultFlightDto2.isPresent());

        FlightDto resultFlightDto1 = optionalResultFlightDto1.get();
        FlightDto resultFlightDto2 = optionalResultFlightDto2.get();

        assertAll(
                () -> assertEquals(expectedFlightDto1.getId(), resultFlightDto1.getId()),
                () -> assertNotNull(resultFlightDto1.getCreationTime()),
                () -> assertNotNull(resultFlightDto1.getUpdateTime()),

                () -> assertEquals(expectedFlightDto2.getId(), resultFlightDto2.getId()),
                () -> assertNotNull(resultFlightDto2.getCreationTime()),
                () -> assertNotNull(resultFlightDto2.getUpdateTime())
        );
    }
}
