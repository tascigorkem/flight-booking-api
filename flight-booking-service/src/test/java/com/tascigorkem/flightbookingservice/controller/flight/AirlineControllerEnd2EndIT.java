package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AirlineRepository;
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
 * End to End Test for AirlineController.class
 * @see AirlineController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AirlineControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AirlineRepository airlineRepository;

    /**
     * E2E test for AirlineController:getAllAirlines
     */
    @Test
    void getAirlineById_WithAirlineId_ShouldReturnAirline() throws JsonProcessingException {
        // GIVEN
        UUID fakeAirlineEntityId = EntityModelFaker.fakeId();
        AirlineEntity fakeAirlineEntity = EntityModelFaker.getFakeAirlineEntity(fakeAirlineEntityId, false);
        airlineRepository.save(fakeAirlineEntity);

        // WHEN
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/airlines/" + fakeAirlineEntityId, String.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode airlineDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeAirlineEntity, airlineDtoJsonNode);

        // clean dummy entities from db
        airlineRepository.delete(fakeAirlineEntity);
    }

    private void assertFieldsBetweenDtoAndJson(AirlineEntity fakeAirlineEntity, JsonNode airlineDtoJsonNode) {
        assertAll(
                () -> assertEquals(airlineDtoJsonNode.path("id").asText(), fakeAirlineEntity.getId().toString()),
                () -> assertEquals(airlineDtoJsonNode.path("name").asText(), fakeAirlineEntity.getName()),
                () -> assertEquals(airlineDtoJsonNode.path("country").asText(), fakeAirlineEntity.getCountry()),

                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("get-airline-by-id-GET")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("all-airlines-GET")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("add-airline-POST")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("update-airline-by-id-with-body-PUT")),
                () -> assertNotNull(airlineDtoJsonNode.path("_links").get("remove-airline-by-id-DELETE"))
        );
    }

}
