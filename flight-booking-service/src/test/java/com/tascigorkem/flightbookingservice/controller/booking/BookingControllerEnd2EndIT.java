package com.tascigorkem.flightbookingservice.controller.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.booking.BookingRepository;
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
 * End to End Test for BookingController.class
 * @see BookingController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookingControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * E2E test for BookingController:getAllBookings
     */
    @Test
    void getBookingById_WithBookingId_ShouldReturnBooking() throws JsonProcessingException {
        // GIVEN
        UUID fakeBookingEntityId = EntityModelFaker.fakeId();
        BookingEntity fakeBookingEntity = EntityModelFaker.getFakeBookingEntity(fakeBookingEntityId, false);
        bookingRepository.save(fakeBookingEntity);

        // WHEN
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/bookings/" + fakeBookingEntityId, String.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode bookingDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeBookingEntity, bookingDtoJsonNode);

        // clean dummy entities from db
        bookingRepository.delete(fakeBookingEntity);
    }

    private void assertFieldsBetweenDtoAndJson(BookingEntity fakeBookingEntity, JsonNode bookingDtoJsonNode) {
        assertAll(
                () -> assertEquals(bookingDtoJsonNode.path("id").asText(), fakeBookingEntity.getId().toString()),
                () -> assertEquals(bookingDtoJsonNode.path("state").asText(), fakeBookingEntity.getState()),
                () -> assertEquals(bookingDtoJsonNode.path("paymentDate").asText().substring(0, 19), fakeBookingEntity.getPaymentDate().toString().substring(0, 19)),
                () -> assertEquals(new BigDecimal(bookingDtoJsonNode.path("paymentAmount").asText()), fakeBookingEntity.getPaymentAmount()),
                () -> assertEquals(bookingDtoJsonNode.path("insurance").asBoolean(), fakeBookingEntity.isInsurance()),
                () -> assertEquals(bookingDtoJsonNode.path("luggage").asInt(), fakeBookingEntity.getLuggage()),

                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("get-booking-by-id-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("all-bookings-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("add-booking-POST")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("update-booking-by-id-with-body-PUT")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("remove-booking-by-id-DELETE"))
        );
    }

}
