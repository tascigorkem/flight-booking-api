package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AirlineMapper.AIRLINE_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AirlineServiceTest {

    private final AirlineRepository airlineRepository = mock(AirlineRepository.class);
    private final AirlineService subject = new AirlineServiceImpl(airlineRepository);

    /**
     * Unit test for AirlineService:getAllAirlines
     */
    @Test
    void getAllAirlines_RetrieveAirlines_ShouldReturnNotDeletedAirlines() {
        // GIVEN
        List<AirlineEntity> fakeAirlineEntityList = Arrays.asList(
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true)
        );

        List<AirlineDto> fakeAirlineDtoList = AIRLINE_MAPPER.toAirlineDtoList(fakeAirlineEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<AirlineEntity> fakePageAirlineEntity = new PageImpl<>(fakeAirlineEntityList, pageable, fakeAirlineEntityList.size());

        when(airlineRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageAirlineEntity);

        // WHEN
        Page<AirlineDto> result = subject.getAllAirlines(pageable);

        // THEN
        assertEquals(fakeAirlineDtoList, result.toList());
        verify(airlineRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for AirlineService:getAirlineById
     */
    @Test
    void getAirlineById_WithAirlineId_ShouldReturnAirline() {
        // GIVEN
        UUID fakeAirlineId = EntityModelFaker.fakeId();
        AirlineEntity fakeAirlineEntity = EntityModelFaker.getFakeAirlineEntity(fakeAirlineId, true);
        AirlineDto expectedAirlineDto = AIRLINE_MAPPER.toAirlineDto(fakeAirlineEntity);

        when(airlineRepository.findById(fakeAirlineId)).thenReturn(Optional.of(fakeAirlineEntity));

        // WHEN
        AirlineDto result = subject.getAirlineById(fakeAirlineId);

        // THEN
        assertEquals(expectedAirlineDto, result);
        verify(airlineRepository).findById(fakeAirlineId);
    }

    /**
     * Unit test for AirlineService:addAirline
     */
    @Test
    void addAirline_SaveIntoDatabase_ShouldSaveAndReturnSavedAirline() {
        // GIVEN
        UUID fakeAirlineId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineId, true);
        AirlineEntity fakeAirlineEntity = AIRLINE_MAPPER.toAirlineEntity(fakeAirlineDto);
        AirlineDto expectedAirlineDto = AIRLINE_MAPPER.toAirlineDto(fakeAirlineEntity);

        when(airlineRepository.save(any(AirlineEntity.class))).thenReturn(fakeAirlineEntity);

        // WHEN
        AirlineDto result = subject.addAirline(fakeAirlineDto);

        // THEN
        assertEquals(expectedAirlineDto, result);
        verify(airlineRepository).save(any(AirlineEntity.class));
    }

    /**
     * Unit test for AirlineService:updateAirline
     */
    @Test
    void updateAirline_SaveIntoDatabase_ShouldSaveAndReturnSavedAirline() {
        // GIVEN
        UUID fakeAirlineId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineId, true);
        AirlineEntity fakeAirlineEntity = AIRLINE_MAPPER.toAirlineEntity(fakeAirlineDto);
        AirlineDto expectedAirlineDto = AIRLINE_MAPPER.toAirlineDto(fakeAirlineEntity);

        when(airlineRepository.findById(fakeAirlineId)).thenReturn(Optional.of(fakeAirlineEntity));
        when(airlineRepository.save(fakeAirlineEntity)).thenReturn(fakeAirlineEntity);

        // WHEN
        AirlineDto result = subject.updateAirline(fakeAirlineDto);

        // THEN
        assertEquals(expectedAirlineDto, result);
        verify(airlineRepository).findById(fakeAirlineId);
        verify(airlineRepository).save(any(AirlineEntity.class));
    }

    /**
     * Unit test for AirlineService:removeAirline
     */
    @Test
    void removeAirline_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedAirline() {
        // GIVEN
        UUID fakeAirlineId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineId, true);
        AirlineEntity fakeAirlineEntity = AIRLINE_MAPPER.toAirlineEntity(fakeAirlineDto);
        AirlineDto expectedAirlineDto = AIRLINE_MAPPER.toAirlineDto(fakeAirlineEntity);

        when(airlineRepository.findById(fakeAirlineId)).thenReturn(Optional.of(fakeAirlineEntity));
        when(airlineRepository.save(fakeAirlineEntity)).thenReturn(fakeAirlineEntity);

        // WHEN
        AirlineDto result = subject.removeAirline(fakeAirlineId);

        // THEN
        assertEquals(expectedAirlineDto, result);
        verify(airlineRepository).findById(fakeAirlineId);
        verify(airlineRepository).save(any(AirlineEntity.class));
    }

}
