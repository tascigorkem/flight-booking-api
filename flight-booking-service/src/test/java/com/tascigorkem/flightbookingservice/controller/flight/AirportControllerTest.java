package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.exception.notfound.AirportNotFoundException;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.flight.AirportService;
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

@WebMvcTest(AirportController.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AirportService airportService;

    /**
     * Unit test for AirportController:getAllAirports
     */
    @Test
    void getAllAirports_RetrieveAirports_ShouldReturnNotDeletedAirports() throws Exception {
        // GIVEN
        List<AirportDto> fakeAirportDtoList = Arrays.asList(
                DtoModelFaker.getFakeAirportDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAirportDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAirportDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AirportDto> fakeAirportDtoPage = new PageImpl<>(fakeAirportDtoList, pageable, fakeAirportDtoList.size());

        when(this.airportService.getAllAirports(any(Pageable.class)))
                .thenReturn(fakeAirportDtoPage);

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

        verify(airportService).getAllAirports(any(Pageable.class));
    }


    /**
     * Unit test for AirportController:getAirportById
     */
    @Test
    void getAirportById_WithAirportId_ShouldReturnAirport() throws Exception {
        // GIVEN
        UUID fakeAirportDtoId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportDtoId, true);

        when(this.airportService.getAirportById(fakeAirportDtoId))
                .thenReturn(fakeAirportDto);

        // WHEN
        this.mockMvc.perform(get("/airports/{id}", fakeAirportDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airportDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirportDto, airportDtoJsonNode);
                });

        verify(airportService).getAirportById(fakeAirportDtoId);
    }

    /**
     * Unit test for AirportController:getAirportById
     * Given wrong Airport id and should return 404 Http Status with AirportNotFoundException message
     */
    @Test
    void getAirportById_WrongAirportId_ShouldReturn404NotFound() throws Exception {
        // GIVEN
        UUID wrongFakeAirportId = DtoModelFaker.fakeId();
        AirportNotFoundException expectedAirportNotFoundException =
                new AirportNotFoundException("id", wrongFakeAirportId.toString());

        when(airportService.getAirportById(wrongFakeAirportId))
                .thenThrow(expectedAirportNotFoundException);

        // WHEN
        this.mockMvc.perform(get("/airports/{id}", wrongFakeAirportId))
                .andDo(print())

                // THEN
                .andExpect(status().isNotFound());

        verify(airportService).getAirportById(wrongFakeAirportId);
    }

    /**
     * Unit test for AirportController:addAirport
     */
    @Test
    void addAirport_SaveIntoDatabase_ShouldSaveAndReturnSavedAirport() throws Exception {
        // GIVEN
        UUID fakeAirportDtoId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportDtoId, true);

        when(this.airportService.addAirport(fakeAirportDto))
                .thenReturn(fakeAirportDto);

        // WHEN
        this.mockMvc.perform(post("/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirportDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airportDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirportDto, airportDtoJsonNode);
                });

        verify(airportService).addAirport(fakeAirportDto);
    }

    /**
     * Unit test for AirportController:addAirport
     */
    @Test
    void addAirport_WithMissingFieldValue_ShouldReturn400BadRequest() throws Exception {
        // GIVEN
        UUID fakeAirportDtoId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportDtoId, true);

        when(this.airportService.addAirport(fakeAirportDto))
                .thenReturn(fakeAirportDto);

        fakeAirportDto.setCode(StringUtils.EMPTY);
        // WHEN
        this.mockMvc.perform(post("/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirportDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isBadRequest());

        verify(airportService, never()).addAirport(fakeAirportDto);
    }

    /**
     * Unit test for AirportController:updateAirport
     */
    @Test
    void updateAirport_SaveIntoDatabase_ShouldSaveAndReturnSavedAirport() throws Exception {
        // GIVEN
        UUID fakeAirportDtoId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportDtoId, true);

        when(this.airportService.updateAirport(fakeAirportDto))
                .thenReturn(fakeAirportDto);

        // WHEN
        this.mockMvc.perform(put("/airports/{id}", fakeAirportDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirportDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airportDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirportDto, airportDtoJsonNode);
                });

        verify(airportService).updateAirport(fakeAirportDto);
    }

    /**
     * Unit test for AirportController:removeAirport
     */
    @Test
    void removeAirport_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedAirport() throws Exception {
        // GIVEN
        UUID fakeAirportDtoId = DtoModelFaker.fakeId();
        AirportDto fakeAirportDto = DtoModelFaker.getFakeAirportDto(fakeAirportDtoId, true);

        when(this.airportService.removeAirport(fakeAirportDtoId))
                .thenReturn(fakeAirportDto);

        // WHEN
        this.mockMvc.perform(delete("/airports/{id}", fakeAirportDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airportDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirportDto, airportDtoJsonNode);
                });

        verify(airportService).removeAirport(fakeAirportDtoId);
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