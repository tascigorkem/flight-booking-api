package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirportRepository;
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

import static com.tascigorkem.flightbookingservice.service.flight.AirportMapper.AIRPORT_MAPPER;
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
class AirportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AirportRepository airportRepository;

    /**
     * Integration test for AirportController:getAllAirports
     * Checking whether connection with AirportService is successful
     */
    @Test
    void getAllAirports_RetrieveAirports_ShouldReturnNotDeletedAirports() throws Exception {
        // GIVEN
        List<AirportEntity> fakeAirportEntityList = Arrays.asList(
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirportEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AirportEntity> fakeAirportEntityPage = new PageImpl<>(fakeAirportEntityList, pageable, fakeAirportEntityList.size());

        when(this.airportRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeAirportEntityPage);

        List<AirportDto> fakeAirportDtoList = AIRPORT_MAPPER.toAirportDtoList(fakeAirportEntityList);
        PageImpl<AirportDto> fakeAirportDtoPage = new PageImpl<>(fakeAirportDtoList, pageable, fakeAirportDtoList.size());

        // WHEN
        this.mockMvc.perform(get("/airports"))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeAirportDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeAirportDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeAirportDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeAirportDtoPage.getNumber())
                    );

                    JsonNode airportDtoListJsonNode = parentJsonNode.path("_embedded").path("airportDtoList");
                    assertEquals(airportDtoListJsonNode.size(), fakeAirportDtoList.size());

                    for (int i = 0; i < airportDtoListJsonNode.size(); i++) {
                        JsonNode airportDtoJsonNode = airportDtoListJsonNode.path(i);
                        AirportDto fakeAirportDto = fakeAirportDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeAirportDto, airportDtoJsonNode);
                    }
                });

        verify(airportRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(AirportDto fakeAirportDto, JsonNode airportDtoJsonNode) {
        assertAll(
                () -> assertEquals(airportDtoJsonNode.path("id").asText(), fakeAirportDto.getId().toString()),
                () -> assertEquals(airportDtoJsonNode.path("name").asText(), fakeAirportDto.getName()),
                () -> assertEquals(airportDtoJsonNode.path("code").asText(), fakeAirportDto.getCode()),
                () -> assertEquals(airportDtoJsonNode.path("city").asText(), fakeAirportDto.getCity()),
                () -> assertEquals(airportDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeAirportDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(airportDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeAirportDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(airportDtoJsonNode.path("_links").get("get-airport-by-id-GET")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("all-airports-GET")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("add-airport-POST")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("update-airport-by-id-with-body-PUT")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("remove-airport-by-id-DELETE"))
        );
    }

}
