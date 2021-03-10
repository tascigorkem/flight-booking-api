package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AircraftRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.tascigorkem.flightbookingservice.service.flight.AircraftMapper.AIRCRAFT_MAPPER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class AircraftControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AircraftRepository aircraftRepository;

    /**
     * Integration test for AircraftController:getAllAircrafts
     * Checking whether connection with AircraftService is successful
     */
    @Test
    void testGetAllAircrafts() throws Exception {
        // arrange
        List<AircraftEntity> fakeAircraftEntityList = Arrays.asList(
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAircraftEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AircraftEntity> fakeAircraftEntityPage = new PageImpl<>(fakeAircraftEntityList, pageable, fakeAircraftEntityList.size());

        when(this.aircraftRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeAircraftEntityPage);

        List<AircraftDto> fakeAircraftDtoList = AIRCRAFT_MAPPER.toAircraftDtoList(fakeAircraftEntityList);
        PageImpl<AircraftDto> fakeAircraftDtoPage = new PageImpl<>(fakeAircraftDtoList, pageable, fakeAircraftDtoList.size());

        // act
        this.mockMvc.perform(get("/aircrafts"))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeAircraftDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeAircraftDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeAircraftDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeAircraftDtoPage.getNumber())
                    );

                    JsonNode aircraftDtoListJsonNode = parentJsonNode.path("_embedded").path("aircraftDtoList");
                    assertEquals(aircraftDtoListJsonNode.size(), fakeAircraftDtoList.size());

                    for (int i = 0; i < aircraftDtoListJsonNode.size(); i++) {
                        JsonNode aircraftDtoJsonNode = aircraftDtoListJsonNode.path(i);
                        AircraftDto fakeAircraftDto = fakeAircraftDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeAircraftDto, aircraftDtoJsonNode);
                    }
                });

        verify(aircraftRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(AircraftDto fakeAircraftDto, JsonNode aircraftDtoJsonNode) {
        assertAll(
                () -> assertEquals(aircraftDtoJsonNode.path("id").asText(), fakeAircraftDto.getId().toString()),
                () -> assertEquals(aircraftDtoJsonNode.path("modelName").asText(), fakeAircraftDto.getModelName()),
                () -> assertEquals(aircraftDtoJsonNode.path("code").asText(), fakeAircraftDto.getCode()),
                () -> assertEquals(aircraftDtoJsonNode.path("seat").asInt(), fakeAircraftDto.getSeat()),
                () -> assertEquals(aircraftDtoJsonNode.path("country").asText(), fakeAircraftDto.getCountry()),
                () -> assertEquals(aircraftDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeAircraftDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(aircraftDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeAircraftDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("get-aircraft-by-id-GET")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("all-aircrafts-GET")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("add-aircraft-POST")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("update-aircraft-by-id-with-body-PUT")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("remove-aircraft-by-id-DELETE"))
        );
    }

}
