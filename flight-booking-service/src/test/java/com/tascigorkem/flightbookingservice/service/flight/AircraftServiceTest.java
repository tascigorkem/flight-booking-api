package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AircraftRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AircraftMapper.AIRCRAFT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AircraftServiceTest {

    private final AircraftRepository aircraftRepository = mock(AircraftRepository.class);
    private final AircraftService subject = new AircraftServiceImpl(aircraftRepository);

    /**
     * Unit test for AircraftService:getAllAircrafts
     */
    @Test
    void getAllAircrafts_RetrieveAircrafts_ShouldReturnNotDeletedAircrafts() {
        // GIVEN
        List<AircraftEntity> fakeAircraftEntityList = Arrays.asList(
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true)
        );

        List<AircraftDto> fakeAircraftDtoList = AIRCRAFT_MAPPER.toAircraftDtoList(fakeAircraftEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<AircraftEntity> fakePageAircraftEntity = new PageImpl<>(fakeAircraftEntityList, pageable, fakeAircraftEntityList.size());

        when(aircraftRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageAircraftEntity);

        // WHEN
        Page<AircraftDto> result = subject.getAllAircrafts(pageable);

        // THEN
        assertEquals(fakeAircraftDtoList, result.toList());
        verify(aircraftRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for AircraftService:getAircraftById
     */
    @Test
    void getAircraftById_WithAircraftId_ShouldReturnAircraft() {
        // GIVEN
        UUID fakeAircraftId = EntityModelFaker.fakeId();
        AircraftEntity fakeAircraftEntity = EntityModelFaker.getFakeAircraftEntity(fakeAircraftId, true);
        AircraftDto expectedAircraftDto = AIRCRAFT_MAPPER.toAircraftDto(fakeAircraftEntity);

        when(aircraftRepository.findById(fakeAircraftId)).thenReturn(Optional.of(fakeAircraftEntity));

        // WHEN
        AircraftDto result = subject.getAircraftById(fakeAircraftId);

        // THEN
        assertEquals(expectedAircraftDto, result);
        verify(aircraftRepository).findById(fakeAircraftId);
    }

    /**
     * Unit test for AircraftService:addAircraft
     */
    @Test
    void addAircraft_SaveIntoDatabase_ShouldSaveAndReturnSavedAircraft() {
        // GIVEN
        UUID fakeAircraftId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftId, true);
        AircraftEntity fakeAircraftEntity = AIRCRAFT_MAPPER.toAircraftEntity(fakeAircraftDto);
        AircraftDto expectedAircraftDto = AIRCRAFT_MAPPER.toAircraftDto(fakeAircraftEntity);

        when(aircraftRepository.save(any(AircraftEntity.class))).thenReturn(fakeAircraftEntity);

        // WHEN
        AircraftDto result = subject.addAircraft(fakeAircraftDto);

        // THEN
        assertEquals(expectedAircraftDto, result);
        verify(aircraftRepository).save(any(AircraftEntity.class));
    }

    /**
     * Unit test for AircraftService:updateAircraft
     */
    @Test
    void updateAircraft_SaveIntoDatabase_ShouldSaveAndReturnSavedAircraft() {
        // GIVEN
        UUID fakeAircraftId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftId, true);
        AircraftEntity fakeAircraftEntity = AIRCRAFT_MAPPER.toAircraftEntity(fakeAircraftDto);
        AircraftDto expectedAircraftDto = AIRCRAFT_MAPPER.toAircraftDto(fakeAircraftEntity);

        when(aircraftRepository.findById(fakeAircraftId)).thenReturn(Optional.of(fakeAircraftEntity));
        when(aircraftRepository.save(fakeAircraftEntity)).thenReturn(fakeAircraftEntity);

        // WHEN
        AircraftDto result = subject.updateAircraft(fakeAircraftDto);

        // THEN
        assertEquals(expectedAircraftDto, result);
        verify(aircraftRepository).findById(fakeAircraftId);
        verify(aircraftRepository).save(any(AircraftEntity.class));
    }

    /**
     * Unit test for AircraftService:removeAircraft
     */
    @Test
    void removeAircraft_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedAircraft() {
        // GIVEN
        UUID fakeAircraftId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftId, true);
        AircraftEntity fakeAircraftEntity = AIRCRAFT_MAPPER.toAircraftEntity(fakeAircraftDto);
        AircraftDto expectedAircraftDto = AIRCRAFT_MAPPER.toAircraftDto(fakeAircraftEntity);

        when(aircraftRepository.findById(fakeAircraftId)).thenReturn(Optional.of(fakeAircraftEntity));
        when(aircraftRepository.save(fakeAircraftEntity)).thenReturn(fakeAircraftEntity);

        // WHEN
        AircraftDto result = subject.removeAircraft(fakeAircraftId);

        // THEN
        assertEquals(expectedAircraftDto, result);
        verify(aircraftRepository).findById(fakeAircraftId);
        verify(aircraftRepository).save(any(AircraftEntity.class));
    }

}
