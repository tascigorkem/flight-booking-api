package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.controller.flight.AircraftController;
import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.flight.AircraftService;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AircraftController.class)
class AircraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AircraftService aircraftService;

    /**
     * Unit test for AircraftController:getAllAircrafts
     */
    @Test
    void testGetAllAircrafts() throws Exception {
        // arrange
        List<AircraftDto> fakeAircraftDtoList = Arrays.asList(
                DtoModelFaker.getFakeAircraftDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAircraftDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAircraftDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AircraftDto> fakeAircraftDtoPage = new PageImpl<>(fakeAircraftDtoList, pageable, fakeAircraftDtoList.size());

        when(this.aircraftService.getAllAircrafts(any(Pageable.class)))
                .thenReturn(fakeAircraftDtoPage);

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

        verify(aircraftService).getAllAircrafts(any(Pageable.class));
    }


    /**
     * Unit test for AircraftController:getAircraftById
     */
    @Test
    void testGetAircraftById() throws Exception {
        // arrange
        UUID fakeAircraftDtoId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftDtoId, true);

        when(this.aircraftService.getAircraftById(fakeAircraftDtoId))
                .thenReturn(fakeAircraftDto);

        // act
        this.mockMvc.perform(get("/aircrafts/{id}", fakeAircraftDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode aircraftDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAircraftDto, aircraftDtoJsonNode);
                });

        verify(aircraftService).getAircraftById(fakeAircraftDtoId);
    }

    /**
     * Unit test for AircraftController:addAircraft
     */
    @Test
    void testAddAircraft() throws Exception {
        // arrange
        UUID fakeAircraftDtoId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftDtoId, true);

        when(this.aircraftService.addAircraft(fakeAircraftDto))
                .thenReturn(fakeAircraftDto);

        // act
        this.mockMvc.perform(post("/aircrafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAircraftDto)))
                .andDo(print())

                // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode aircraftDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAircraftDto, aircraftDtoJsonNode);
                });

        verify(aircraftService).addAircraft(fakeAircraftDto);
    }

    /**
     * Unit test for AircraftController:addAircraft
     */
    @Test
    void testAddAircraftWithBlankName_return400_BadRequest() throws Exception {
        // arrange
        UUID fakeAircraftDtoId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftDtoId, true);

        when(this.aircraftService.addAircraft(fakeAircraftDto))
                .thenReturn(fakeAircraftDto);

        fakeAircraftDto.setCode(StringUtils.EMPTY);
        // act
        this.mockMvc.perform(post("/aircrafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAircraftDto)))
                .andDo(print())

                // assert
                .andExpect(status().isBadRequest());

        verify(aircraftService, never()).addAircraft(fakeAircraftDto);
    }

    /**
     * Unit test for AircraftController:updateAircraft
     */
    @Test
    void testUpdateAircraft() throws Exception {
        // arrange
        UUID fakeAircraftDtoId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftDtoId, true);

        when(this.aircraftService.updateAircraft(fakeAircraftDto))
                .thenReturn(fakeAircraftDto);

        // act
        this.mockMvc.perform(put("/aircrafts/{id}", fakeAircraftDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAircraftDto)))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode aircraftDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAircraftDto, aircraftDtoJsonNode);
                });

        verify(aircraftService).updateAircraft(fakeAircraftDto);
    }

    /**
     * Unit test for AircraftController:removeAircraft
     */
    @Test
    void testRemoveAircraft() throws Exception {
        // arrange
        UUID fakeAircraftDtoId = DtoModelFaker.fakeId();
        AircraftDto fakeAircraftDto = DtoModelFaker.getFakeAircraftDto(fakeAircraftDtoId, true);

        when(this.aircraftService.removeAircraft(fakeAircraftDtoId))
                .thenReturn(fakeAircraftDto);

        // act
        this.mockMvc.perform(delete("/aircrafts/{id}", fakeAircraftDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode aircraftDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAircraftDto, aircraftDtoJsonNode);
                });

        verify(aircraftService).removeAircraft(fakeAircraftDtoId);
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