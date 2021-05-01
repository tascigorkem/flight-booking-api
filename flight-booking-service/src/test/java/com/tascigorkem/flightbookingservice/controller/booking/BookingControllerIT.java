package com.tascigorkem.flightbookingservice.controller.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.booking.BookingRepository;
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

import static com.tascigorkem.flightbookingservice.service.booking.BookingMapper.BOOKING_MAPPER;
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
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingRepository bookingRepository;

    /**
     * Integration test for BookingController:getAllBookings
     * Checking whether connection with BookingService is successful
     */
    @Test
    void getAllBookings_RetrieveBookings_ShouldReturnNotDeletedBookings() throws Exception {
        // GIVEN
        List<BookingEntity> fakeBookingEntityList = Arrays.asList(
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeBookingEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<BookingEntity> fakeBookingEntityPage = new PageImpl<>(fakeBookingEntityList, pageable, fakeBookingEntityList.size());

        when(this.bookingRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeBookingEntityPage);

        List<BookingDto> fakeBookingDtoList = BOOKING_MAPPER.toBookingDtoList(fakeBookingEntityList);
        PageImpl<BookingDto> fakeBookingDtoPage = new PageImpl<>(fakeBookingDtoList, pageable, fakeBookingDtoList.size());

        // WHEN
        this.mockMvc.perform(get("/bookings"))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeBookingDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeBookingDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeBookingDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeBookingDtoPage.getNumber())
                    );

                    JsonNode bookingDtoListJsonNode = parentJsonNode.path("_embedded").path("bookingDtoList");
                    assertEquals(bookingDtoListJsonNode.size(), fakeBookingDtoList.size());

                    for (int i = 0; i < bookingDtoListJsonNode.size(); i++) {
                        JsonNode bookingDtoJsonNode = bookingDtoListJsonNode.path(i);
                        BookingDto fakeBookingDto = fakeBookingDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeBookingDto, bookingDtoJsonNode);
                    }
                });

        verify(bookingRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(BookingDto fakeBookingDto, JsonNode bookingDtoJsonNode) {
        assertAll(
                () -> assertEquals(bookingDtoJsonNode.path("id").asText(), fakeBookingDto.getId().toString()),
                () -> assertEquals(bookingDtoJsonNode.path("state").asText(), fakeBookingDto.getState()),
                () -> assertEquals(bookingDtoJsonNode.path("paymentDate").asText().substring(0, 19), fakeBookingDto.getPaymentDate().toString().substring(0, 19)),
                () -> assertEquals(new BigDecimal(bookingDtoJsonNode.path("paymentAmount").asText()), fakeBookingDto.getPaymentAmount()),
                () -> assertEquals(bookingDtoJsonNode.path("insurance").asBoolean(), fakeBookingDto.isInsurance()),
                () -> assertEquals(bookingDtoJsonNode.path("luggage").asInt(), fakeBookingDto.getLuggage()),
                () -> assertEquals(bookingDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeBookingDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(bookingDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeBookingDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertEquals(bookingDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeBookingDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(bookingDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeBookingDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("get-booking-by-id-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("all-bookings-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("add-booking-POST")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("update-booking-by-id-with-body-PUT")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("remove-booking-by-id-DELETE"))
        );
    }

}
