package com.tascigorkem.flightbookingservice.service.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.flight.AirportMapper.AIRPORT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AirportServiceTest {

    private final AirportRepository airportRepository = mock(AirportRepository.class);
    private final AirportService subject = new AirportServiceImpl(airportRepository);

    /**
     * Unit test for AirportService:getAllAirports
     */
    @Test
    void testGetAllAirports() {
        // arrange
        List<AirportEntity> fakeAirportEntityList = Arrays.asList(
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true)
        );

        List<AirportDto> fakeAirportDtoList = AIRPORT_MAPPER.toAirportDtoList(fakeAirportEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<AirportEntity> fakePageAirportEntity = new PageImpl<>(fakeAirportEntityList, pageable, fakeAirportEntityList.size());

        when(airportRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageAirportEntity);

        // act
        Page<AirportDto> result = subject.getAllAirports(pageable);

        // assert
        assertEquals(fakeAirportDtoList, result.toList());
        verify(airportRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for AirportService:getAirportById
     */
    @Test
    void testGetAirportById() {
        // arrange
        UUID fakeAirportId = EntityModelFaker.fakeId();
        AirportEntity fakeAirportEntity = EntityModelFaker.getFakeAirportEntity(fakeAirportId, true);
        AirportDto expectedAirportDto = AIRPORT_MAPPER.toAirportDto(fakeAirportEntity);

        when(airportRepository.findById(fakeAirportId)).thenReturn(Optional.of(fakeAirportEntity));

        // act
        AirportDto result = subject.getAirportById(fakeAirportId);

        // assert
        assertEquals(expectedAirportDto, result);
        verify(airportRepository).findById(fakeAirportId);
    }

    /**
     * Unit test for AirportService:addAirport
     */
    @Test
    void testAddAirport() {
        // arrange
        UUID fakeAirportId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportId, true);
        AirportEntity fakeAirportEntity = AIRPORT_MAPPER.toAirportEntity(fakeAirportDto);
        AirportDto expectedAirportDto = AIRPORT_MAPPER.toAirportDto(fakeAirportEntity);

        when(airportRepository.save(any(AirportEntity.class))).thenReturn(fakeAirportEntity);

        // act
        AirportDto result = subject.addAirport(fakeAirportDto);

        // assert
        assertEquals(expectedAirportDto, result);
        verify(airportRepository).save(any(AirportEntity.class));
    }

    /**
     * Unit test for AirportService:updateAirport
     */
    @Test
    void testUpdateAirport() {
        // arrange
        UUID fakeAirportId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportId, true);
        AirportEntity fakeAirportEntity = AIRPORT_MAPPER.toAirportEntity(fakeAirportDto);
        AirportDto expectedAirportDto = AIRPORT_MAPPER.toAirportDto(fakeAirportEntity);

        when(airportRepository.findById(fakeAirportId)).thenReturn(Optional.of(fakeAirportEntity));
        when(airportRepository.save(fakeAirportEntity)).thenReturn(fakeAirportEntity);

        // act
        AirportDto result = subject.updateAirport(fakeAirportDto);

        // assert
        assertEquals(expectedAirportDto, result);
        verify(airportRepository).findById(fakeAirportId);
        verify(airportRepository).save(any(AirportEntity.class));
    }

    /**
     * Unit test for AirportService:removeAirport
     */
    @Test
    void testRemoveAirport() {
        // arrange
        UUID fakeAirportId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportId, true);
        AirportEntity fakeAirportEntity = AIRPORT_MAPPER.toAirportEntity(fakeAirportDto);
        AirportDto expectedAirportDto = AIRPORT_MAPPER.toAirportDto(fakeAirportEntity);

        when(airportRepository.findById(fakeAirportId)).thenReturn(Optional.of(fakeAirportEntity));
        when(airportRepository.save(fakeAirportEntity)).thenReturn(fakeAirportEntity);

        // act
        AirportDto result = subject.removeAirport(fakeAirportId);

        // assert
        assertEquals(expectedAirportDto, result);
        verify(airportRepository).findById(fakeAirportId);
        verify(airportRepository).save(any(AirportEntity.class));
    }

}
