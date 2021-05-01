package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.FlightMapper.FLIGHT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    private final FlightRepository flightRepository = mock(FlightRepository.class);
    private final FlightService subject = new FlightServiceImpl(flightRepository);

    /**
     * Unit test for FlightService:getAllFlights
     */
    @Test
    void getAllFlights_RetrieveFlights_ShouldReturnNotDeletedFlights() {
        // GIVEN
        List<FlightEntity> fakeFlightEntityList = Arrays.asList(
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true)
        );

        List<FlightDto> fakeFlightDtoList = FLIGHT_MAPPER.toFlightDtoList(fakeFlightEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<FlightEntity> fakePageFlightEntity = new PageImpl<>(fakeFlightEntityList, pageable, fakeFlightEntityList.size());

        when(flightRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageFlightEntity);

        // WHEN
        Page<FlightDto> result = subject.getAllFlights(pageable);

        // THEN
        assertEquals(fakeFlightDtoList, result.toList());
        verify(flightRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for FlightService:getFlightById
     */
    @Test
    void getFlightById_WithFlightId_ShouldReturnFlight() {
        // GIVEN
        UUID fakeFlightId = EntityModelFaker.fakeId();
        FlightEntity fakeFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeFlightId, true);
        FlightDto expectedFlightDto = FLIGHT_MAPPER.toFlightDto(fakeFlightEntity);

        when(flightRepository.findById(fakeFlightId)).thenReturn(Optional.of(fakeFlightEntity));

        // WHEN
        FlightDto result = subject.getFlightById(fakeFlightId);

        // THEN
        assertEquals(expectedFlightDto, result);
        verify(flightRepository).findById(fakeFlightId);
    }

    /**
     * Unit test for FlightService:addFlight
     */
    @Test
    void addFlight_SaveIntoDatabase_ShouldSaveAndReturnSavedFlight() {
        // GIVEN
        UUID fakeFlightId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightId, true);
        FlightEntity fakeFlightEntity = FLIGHT_MAPPER.toFlightEntity(fakeFlightDto);
        FlightDto expectedFlightDto = FLIGHT_MAPPER.toFlightDto(fakeFlightEntity);

        when(flightRepository.save(any(FlightEntity.class))).thenReturn(fakeFlightEntity);

        // WHEN
        FlightDto result = subject.addFlight(fakeFlightDto);

        // THEN
        assertEquals(expectedFlightDto, result);
        verify(flightRepository).save(any(FlightEntity.class));
    }

    /**
     * Unit test for FlightService:updateFlight
     */
    @Test
    void updateFlight_SaveIntoDatabase_ShouldSaveAndReturnSavedFlight() {
        // GIVEN
        UUID fakeFlightId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightId, true);
        FlightEntity fakeFlightEntity = FLIGHT_MAPPER.toFlightEntity(fakeFlightDto);
        FlightDto expectedFlightDto = FLIGHT_MAPPER.toFlightDto(fakeFlightEntity);

        when(flightRepository.findById(fakeFlightId)).thenReturn(Optional.of(fakeFlightEntity));
        when(flightRepository.save(fakeFlightEntity)).thenReturn(fakeFlightEntity);

        // WHEN
        FlightDto result = subject.updateFlight(fakeFlightDto);

        // THEN
        assertEquals(expectedFlightDto, result);
        verify(flightRepository).findById(fakeFlightId);
        verify(flightRepository).save(any(FlightEntity.class));
    }

    /**
     * Unit test for FlightService:removeFlight
     */
    @Test
    void removeFlight_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedFlight() {
        // GIVEN
        UUID fakeFlightId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightId, true);
        FlightEntity fakeFlightEntity = FLIGHT_MAPPER.toFlightEntity(fakeFlightDto);
        FlightDto expectedFlightDto = FLIGHT_MAPPER.toFlightDto(fakeFlightEntity);

        when(flightRepository.findById(fakeFlightId)).thenReturn(Optional.of(fakeFlightEntity));
        when(flightRepository.save(fakeFlightEntity)).thenReturn(fakeFlightEntity);

        // WHEN
        FlightDto result = subject.removeFlight(fakeFlightId);

        // THEN
        assertEquals(expectedFlightDto, result);
        verify(flightRepository).findById(fakeFlightId);
        verify(flightRepository).save(any(FlightEntity.class));
    }

}
