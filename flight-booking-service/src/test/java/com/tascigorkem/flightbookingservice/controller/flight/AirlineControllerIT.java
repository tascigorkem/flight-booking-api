package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirlineRepository;
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

import static com.tascigorkem.flightbookingservice.service.flight.AirlineMapper.AIRLINE_MAPPER;
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
class AirlineControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AirlineRepository airlineRepository;

    /**
     * Integration test for AirlineController:getAllAirlines
     * Checking whether connection with AirlineService is successful
     */
    @Test
    void testGetAllAirlines() throws Exception {
        // arrange
        List<AirlineEntity> fakeAirlineEntityList = Arrays.asList(
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeAirlineEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AirlineEntity> fakeAirlineEntityPage = new PageImpl<>(fakeAirlineEntityList, pageable, fakeAirlineEntityList.size());

        when(this.airlineRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeAirlineEntityPage);

        List<AirlineDto> fakeAirlineDtoList = AIRLINE_MAPPER.toAirlineDtoList(fakeAirlineEntityList);
        PageImpl<AirlineDto> fakeAirlineDtoPage = new PageImpl<>(fakeAirlineDtoList, pageable, fakeAirlineDtoList.size());

        // act
        this.mockMvc.perform(get("/airlines"))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeAirlineDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeAirlineDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeAirlineDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeAirlineDtoPage.getNumber())
                    );

                    JsonNode airlineDtoListJsonNode = parentJsonNode.path("_embedded").path("airlineDtoList");
                    assertEquals(airlineDtoListJsonNode.size(), fakeAirlineDtoList.size());

                    for (int i = 0; i < airlineDtoListJsonNode.size(); i++) {
                        JsonNode airlineDtoJsonNode = airlineDtoListJsonNode.path(i);
                        AirlineDto fakeAirlineDto = fakeAirlineDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeAirlineDto, airlineDtoJsonNode);
                    }
                });

        verify(airlineRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(AirlineDto fakeAirlineDto, JsonNode airlineDtoJsonNode) {
        assertAll(
                () -> assertEquals(airlineDtoJsonNode.path("id").asText(), fakeAirlineDto.getId().toString()),
                () -> assertEquals(airlineDtoJsonNode.path("name").asText(), fakeAirlineDto.getName()),
                () -> assertEquals(airlineDtoJsonNode.path("country").asText(), fakeAirlineDto.getCountry()),
                () -> assertEquals(airlineDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeAirlineDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(airlineDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeAirlineDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("get-airline-by-id-GET")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("all-airlines-GET")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("add-airline-POST")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("update-airline-by-id-with-body-PUT")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("remove-airline-by-id-DELETE"))
        );
    }

}
