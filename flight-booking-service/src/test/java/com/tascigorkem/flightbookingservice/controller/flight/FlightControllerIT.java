package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.FlightRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.tascigorkem.flightbookingservice.service.flight.FlightMapper.FLIGHT_MAPPER;
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
class FlightControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightRepository flightRepository;

    /**
     * Integration test for FlightController:getAllFlights
     * Checking whether connection with FlightService is successful
     */
    @Test
    void testGetAllFlights() throws Exception {
        // arrange
        List<FlightEntity> fakeFlightEntityList = Arrays.asList(
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeFlightEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<FlightEntity> fakeFlightEntityPage = new PageImpl<>(fakeFlightEntityList, pageable, fakeFlightEntityList.size());

        when(this.flightRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeFlightEntityPage);

        List<FlightDto> fakeFlightDtoList = FLIGHT_MAPPER.toFlightDtoList(fakeFlightEntityList);
        PageImpl<FlightDto> fakeFlightDtoPage = new PageImpl<>(fakeFlightDtoList, pageable, fakeFlightDtoList.size());

        // act
        this.mockMvc.perform(get("/flights"))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeFlightDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeFlightDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeFlightDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeFlightDtoPage.getNumber())
                    );

                    JsonNode flightDtoListJsonNode = parentJsonNode.path("_embedded").path("flightDtoList");
                    assertEquals(flightDtoListJsonNode.size(), fakeFlightDtoList.size());

                    for (int i = 0; i < flightDtoListJsonNode.size(); i++) {
                        JsonNode flightDtoJsonNode = flightDtoListJsonNode.path(i);
                        FlightDto fakeFlightDto = fakeFlightDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeFlightDto, flightDtoJsonNode);
                    }
                });

        verify(flightRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(FlightDto fakeFlightDto, JsonNode flightDtoJsonNode) {
        assertAll(
                () -> assertEquals(flightDtoJsonNode.path("id").asText(), fakeFlightDto.getId().toString()),
                () -> assertEquals(flightDtoJsonNode.path("departureDate").asText().substring(0, 19), fakeFlightDto.getDepartureDate().toString().substring(0, 19)),
                () -> assertEquals(flightDtoJsonNode.path("arrivalDate").asText().substring(0, 19), fakeFlightDto.getArrivalDate().toString().substring(0, 19)),
                () -> assertEquals(new BigDecimal(flightDtoJsonNode.path("price").asText()), fakeFlightDto.getPrice()),
                () -> assertEquals(flightDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeFlightDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(flightDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeFlightDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(flightDtoJsonNode.path("_links").get("get-flight-by-id-GET")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("all-flights-GET")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("add-flight-POST")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("update-flight-by-id-with-body-PUT")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("remove-flight-by-id-DELETE"))
        );
    }

}
