package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.flight.FlightService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightService flightService;

    /**
     * Unit test for FlightController:getAllFlights
     */
    @Test
    void testGetAllFlights() throws Exception {
        // arrange
        List<FlightDto> fakeFlightDtoList = Arrays.asList(
                DtoModelFaker.getFakeFlightDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeFlightDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeFlightDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<FlightDto> fakeFlightDtoPage = new PageImpl<>(fakeFlightDtoList, pageable, fakeFlightDtoList.size());

        when(this.flightService.getAllFlights(any(Pageable.class)))
                .thenReturn(fakeFlightDtoPage);

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

        verify(flightService).getAllFlights(any(Pageable.class));
    }


    /**
     * Unit test for FlightController:getFlightById
     */
    @Test
    void testGetFlightById() throws Exception {
        // arrange
        UUID fakeFlightDtoId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightDtoId, true);

        when(this.flightService.getFlightById(fakeFlightDtoId))
                .thenReturn(fakeFlightDto);

        // act
        this.mockMvc.perform(get("/flights/{id}", fakeFlightDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode flightDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeFlightDto, flightDtoJsonNode);
                });

        verify(flightService).getFlightById(fakeFlightDtoId);
    }

    /**
     * Unit test for FlightController:addFlight
     */
    @Test
    void testAddFlight() throws Exception {
        // arrange
        UUID fakeFlightDtoId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightDtoId, true);

        when(this.flightService.addFlight(fakeFlightDto))
                .thenReturn(fakeFlightDto);

        // act
        this.mockMvc.perform(post("/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeFlightDto)))
                .andDo(print())

                // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode flightDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeFlightDto, flightDtoJsonNode);
                });

        verify(flightService).addFlight(fakeFlightDto);
    }

    /**
     * Unit test for FlightController:addFlight
     */
    @Test
    void testAddFlightWithBlankName_return400_BadRequest() throws Exception {
        // arrange
        UUID fakeFlightDtoId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightDtoId, true);

        when(this.flightService.addFlight(fakeFlightDto))
                .thenReturn(fakeFlightDto);

        fakeFlightDto.setPrice(null);
        // act
        this.mockMvc.perform(post("/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeFlightDto)))
                .andDo(print())

                // assert
                .andExpect(status().isBadRequest());

        verify(flightService, never()).addFlight(fakeFlightDto);
    }

    /**
     * Unit test for FlightController:updateFlight
     */
    @Test
    void testUpdateFlight() throws Exception {
        // arrange
        UUID fakeFlightDtoId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightDtoId, true);

        when(this.flightService.updateFlight(fakeFlightDto))
                .thenReturn(fakeFlightDto);

        // act
        this.mockMvc.perform(put("/flights/{id}", fakeFlightDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeFlightDto)))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode flightDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeFlightDto, flightDtoJsonNode);
                });

        verify(flightService).updateFlight(fakeFlightDto);
    }

    /**
     * Unit test for FlightController:removeFlight
     */
    @Test
    void testRemoveFlight() throws Exception {
        // arrange
        UUID fakeFlightDtoId = DtoModelFaker.fakeId();
        FlightDto fakeFlightDto = DtoModelFaker.getFakeFlightDto(fakeFlightDtoId, true);

        when(this.flightService.removeFlight(fakeFlightDtoId))
                .thenReturn(fakeFlightDto);

        // act
        this.mockMvc.perform(delete("/flights/{id}", fakeFlightDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode flightDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeFlightDto, flightDtoJsonNode);
                });

        verify(flightService).removeFlight(fakeFlightDtoId);
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