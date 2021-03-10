package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.controller.flight.AircraftController;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.AircraftRepository;
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
 * End to End Test for AircraftController.class
 * @see AircraftController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AircraftControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AircraftRepository aircraftRepository;

    /**
     * E2E test for AircraftController:getAllAircrafts
     */
    @Test
    void testGetAircraftById() throws JsonProcessingException {
        // arrange
        UUID fakeAircraftEntityId = EntityModelFaker.fakeId();
        AircraftEntity fakeAircraftEntity = EntityModelFaker.getFakeAircraftEntity(fakeAircraftEntityId, false);
        aircraftRepository.save(fakeAircraftEntity);

        // act
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/aircrafts/" + fakeAircraftEntityId, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode aircraftDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeAircraftEntity, aircraftDtoJsonNode);

        // clean dummy entities from db
        aircraftRepository.delete(fakeAircraftEntity);
    }

    private void assertFieldsBetweenDtoAndJson(AircraftEntity fakeAircraftEntity, JsonNode aircraftDtoJsonNode) {
        assertAll(
                () -> assertEquals(aircraftDtoJsonNode.path("id").asText(), fakeAircraftEntity.getId().toString()),
                () -> assertEquals(aircraftDtoJsonNode.path("modelName").asText(), fakeAircraftEntity.getModelName()),
                () -> assertEquals(aircraftDtoJsonNode.path("code").asText(), fakeAircraftEntity.getCode()),
                () -> assertEquals(aircraftDtoJsonNode.path("seat").asInt(), fakeAircraftEntity.getSeat()),
                () -> assertEquals(aircraftDtoJsonNode.path("country").asText(), fakeAircraftEntity.getCountry()),

                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("get-aircraft-by-id-GET")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("all-aircrafts-GET")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("add-aircraft-POST")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("update-aircraft-by-id-with-body-PUT")),
                () -> assertNotNull(aircraftDtoJsonNode.path("_links").get("remove-aircraft-by-id-DELETE"))
        );
    }

}
