package com.tascigorkem.flightbookingservice.controller.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.flight.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * End to End Test for FlightController.class
 * @see FlightController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class FlightControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightRepository flightRepository;

    /**
     * E2E test for FlightController:getAllFlights
     */
    @Test
    void testGetFlightById() throws JsonProcessingException {
        // arrange
        UUID fakeFlightEntityId = EntityModelFaker.fakeId();
        FlightEntity fakeFlightEntity = EntityModelFaker.getFakeFlightEntity(fakeFlightEntityId, false);
        flightRepository.save(fakeFlightEntity);

        // act
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/flights/" + fakeFlightEntityId, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode flightDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeFlightEntity, flightDtoJsonNode);

        // clean dummy entities from db
        flightRepository.delete(fakeFlightEntity);
    }

    private void assertFieldsBetweenDtoAndJson(FlightEntity fakeFlightEntity, JsonNode flightDtoJsonNode) {
        assertAll(
                () -> assertEquals(flightDtoJsonNode.path("id").asText(), fakeFlightEntity.getId().toString()),
                () -> assertEquals(flightDtoJsonNode.path("departureDate").asText().substring(0, 19), fakeFlightEntity.getDepartureDate().toString().substring(0, 19)),
                () -> assertEquals(flightDtoJsonNode.path("arrivalDate").asText().substring(0, 19), fakeFlightEntity.getArrivalDate().toString().substring(0, 19)),
                () -> assertEquals(new BigDecimal(flightDtoJsonNode.path("price").asText()), fakeFlightEntity.getPrice()),

                () -> assertNotNull(flightDtoJsonNode.path("_links").get("get-flight-by-id-GET")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("all-flights-GET")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("add-flight-POST")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("update-flight-by-id-with-body-PUT")),
                () -> assertNotNull(flightDtoJsonNode.path("_links").get("remove-flight-by-id-DELETE"))
        );
    }

}
