package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * End to End Test for AirportController.class
 * @see AirportController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AirportControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AirportRepository airportRepository;

    /**
     * E2E test for AirportController:getAllAirports
     */
    @Test
    void testGetAirportById() throws JsonProcessingException {
        // arrange
        UUID fakeAirportEntityId = EntityModelFaker.fakeId();
        AirportEntity fakeAirportEntity = EntityModelFaker.getFakeAirportEntity(fakeAirportEntityId, false);
        airportRepository.save(fakeAirportEntity);

        // act
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/airports/" + fakeAirportEntityId, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode airportDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeAirportEntity, airportDtoJsonNode);

        // clean dummy entities from db
        airportRepository.delete(fakeAirportEntity);
    }

    private void assertFieldsBetweenDtoAndJson(AirportEntity fakeAirportEntity, JsonNode airportDtoJsonNode) {
        assertAll(
                () -> assertEquals(airportDtoJsonNode.path("id").asText(), fakeAirportEntity.getId().toString()),
                () -> assertEquals(airportDtoJsonNode.path("name").asText(), fakeAirportEntity.getName()),
                () -> assertEquals(airportDtoJsonNode.path("code").asText(), fakeAirportEntity.getCode()),
                () -> assertEquals(airportDtoJsonNode.path("city").asText(), fakeAirportEntity.getCity()),

                () -> assertNotNull(airportDtoJsonNode.path("_links").get("get-airport-by-id-GET")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("all-airports-GET")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("add-airport-POST")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("update-airport-by-id-with-body-PUT")),
                () -> assertNotNull(airportDtoJsonNode.path("_links").get("remove-airport-by-id-DELETE"))
        );
    }

}
