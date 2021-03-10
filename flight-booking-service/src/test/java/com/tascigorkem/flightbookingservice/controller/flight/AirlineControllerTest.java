package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.flight.AirlineService;
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

@WebMvcTest(AirlineController.class)
class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AirlineService airlineService;

    /**
     * Unit test for AirlineController:getAllAirlines
     */
    @Test
    void testGetAllAirlines() throws Exception {
        // arrange
        List<AirlineDto> fakeAirlineDtoList = Arrays.asList(
                DtoModelFaker.getFakeAirlineDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAirlineDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeAirlineDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<AirlineDto> fakeAirlineDtoPage = new PageImpl<>(fakeAirlineDtoList, pageable, fakeAirlineDtoList.size());

        when(this.airlineService.getAllAirlines(any(Pageable.class)))
                .thenReturn(fakeAirlineDtoPage);

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

        verify(airlineService).getAllAirlines(any(Pageable.class));
    }


    /**
     * Unit test for AirlineController:getAirlineById
     */
    @Test
    void testGetAirlineById() throws Exception {
        // arrange
        UUID fakeAirlineDtoId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineDtoId, true);

        when(this.airlineService.getAirlineById(fakeAirlineDtoId))
                .thenReturn(fakeAirlineDto);

        // act
        this.mockMvc.perform(get("/airlines/{id}", fakeAirlineDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airlineDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirlineDto, airlineDtoJsonNode);
                });

        verify(airlineService).getAirlineById(fakeAirlineDtoId);
    }

    /**
     * Unit test for AirlineController:addAirline
     */
    @Test
    void testAddAirline() throws Exception {
        // arrange
        UUID fakeAirlineDtoId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineDtoId, true);

        when(this.airlineService.addAirline(fakeAirlineDto))
                .thenReturn(fakeAirlineDto);

        // act
        this.mockMvc.perform(post("/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirlineDto)))
                .andDo(print())

                // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airlineDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirlineDto, airlineDtoJsonNode);
                });

        verify(airlineService).addAirline(fakeAirlineDto);
    }

    /**
     * Unit test for AirlineController:addAirline
     */
    @Test
    void testAddAirlineWithBlankName_return400_BadRequest() throws Exception {
        // arrange
        UUID fakeAirlineDtoId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineDtoId, true);

        when(this.airlineService.addAirline(fakeAirlineDto))
                .thenReturn(fakeAirlineDto);

        fakeAirlineDto.setName(StringUtils.EMPTY);
        // act
        this.mockMvc.perform(post("/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirlineDto)))
                .andDo(print())

                // assert
                .andExpect(status().isBadRequest());

        verify(airlineService, never()).addAirline(fakeAirlineDto);
    }

    /**
     * Unit test for AirlineController:updateAirline
     */
    @Test
    void testUpdateAirline() throws Exception {
        // arrange
        UUID fakeAirlineDtoId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineDtoId, true);

        when(this.airlineService.updateAirline(fakeAirlineDto))
                .thenReturn(fakeAirlineDto);

        // act
        this.mockMvc.perform(put("/airlines/{id}", fakeAirlineDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeAirlineDto)))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airlineDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirlineDto, airlineDtoJsonNode);
                });

        verify(airlineService).updateAirline(fakeAirlineDto);
    }

    /**
     * Unit test for AirlineController:removeAirline
     */
    @Test
    void testRemoveAirline() throws Exception {
        // arrange
        UUID fakeAirlineDtoId = DtoModelFaker.fakeId();
        AirlineDto fakeAirlineDto = DtoModelFaker.getFakeAirlineDto(fakeAirlineDtoId, true);

        when(this.airlineService.removeAirline(fakeAirlineDtoId))
                .thenReturn(fakeAirlineDto);

        // act
        this.mockMvc.perform(delete("/airlines/{id}", fakeAirlineDtoId))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode airlineDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeAirlineDto, airlineDtoJsonNode);
                });

        verify(airlineService).removeAirline(fakeAirlineDtoId);
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