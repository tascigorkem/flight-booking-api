package com.tascigorkem.flightbookingservice.controller.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.exception.notfound.BookingNotFoundException;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.booking.BookingService;
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

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    /**
     * Unit test for BookingController:getAllBookings
     */
    @Test
    void getAllBookings_RetrieveBookings_ShouldReturnNotDeletedBookings() throws Exception {
        // GIVEN
        List<BookingDto> fakeBookingDtoList = Arrays.asList(
                DtoModelFaker.getFakeBookingDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeBookingDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeBookingDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<BookingDto> fakeBookingDtoPage = new PageImpl<>(fakeBookingDtoList, pageable, fakeBookingDtoList.size());

        when(this.bookingService.getAllBookings(any(Pageable.class)))
                .thenReturn(fakeBookingDtoPage);

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

        verify(bookingService).getAllBookings(any(Pageable.class));
    }


    /**
     * Unit test for BookingController:getBookingById
     */
    @Test
    void getBookingById_WithBookingId_ShouldReturnBooking() throws Exception {
        // GIVEN
        UUID fakeBookingDtoId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingDtoId, true);

        when(this.bookingService.getBookingById(fakeBookingDtoId))
                .thenReturn(fakeBookingDto);

        // WHEN
        this.mockMvc.perform(get("/bookings/{id}", fakeBookingDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode bookingDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeBookingDto, bookingDtoJsonNode);
                });

        verify(bookingService).getBookingById(fakeBookingDtoId);
    }

    /**
     * Unit test for BookingController:getBookingById
     * Given wrong Booking id and should return 404 Http Status with BookingNotFoundException message
     */
    @Test
    void getBookingById_WrongBookingId_ShouldReturn404NotFound() throws Exception {
        // GIVEN
        UUID wrongFakeBookingId = DtoModelFaker.fakeId();
        BookingNotFoundException expectedBookingNotFoundException =
                new BookingNotFoundException("id", wrongFakeBookingId.toString());

        when(bookingService.getBookingById(wrongFakeBookingId))
                .thenThrow(expectedBookingNotFoundException);

        // WHEN
        this.mockMvc.perform(get("/bookings/{id}", wrongFakeBookingId))
                .andDo(print())

                // THEN
                .andExpect(status().isNotFound());

        verify(bookingService).getBookingById(wrongFakeBookingId);
    }

    /**
     * Unit test for BookingController:addBooking
     */
    @Test
    void addBooking_SaveIntoDatabase_ShouldSaveAndReturnSavedBooking() throws Exception {
        // GIVEN
        UUID fakeBookingDtoId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingDtoId, true);

        when(this.bookingService.addBooking(fakeBookingDto))
                .thenReturn(fakeBookingDto);

        // WHEN
        this.mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeBookingDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode bookingDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeBookingDto, bookingDtoJsonNode);
                });

        verify(bookingService).addBooking(fakeBookingDto);
    }

    /**
     * Unit test for BookingController:addBooking
     */
    @Test
    void addBooking_WithMissingFieldValue_ShouldReturn400BadRequest() throws Exception {
        // GIVEN
        UUID fakeBookingDtoId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingDtoId, true);

        when(this.bookingService.addBooking(fakeBookingDto))
                .thenReturn(fakeBookingDto);

        fakeBookingDto.setState(StringUtils.EMPTY);
        // WHEN
        this.mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeBookingDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(fakeBookingDto);
    }

    /**
     * Unit test for BookingController:updateBooking
     */
    @Test
    void updateBooking_SaveIntoDatabase_ShouldSaveAndReturnSavedBooking() throws Exception {
        // GIVEN
        UUID fakeBookingDtoId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingDtoId, true);

        when(this.bookingService.updateBooking(fakeBookingDto))
                .thenReturn(fakeBookingDto);

        // WHEN
        this.mockMvc.perform(put("/bookings/{id}", fakeBookingDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeBookingDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode bookingDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeBookingDto, bookingDtoJsonNode);
                });

        verify(bookingService).updateBooking(fakeBookingDto);
    }

    /**
     * Unit test for BookingController:removeBooking
     */
    @Test
    void removeBooking_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedBooking() throws Exception {
        // GIVEN
        UUID fakeBookingDtoId = DtoModelFaker.fakeId();
        BookingDto fakeBookingDto = DtoModelFaker.getFakeBookingDto(fakeBookingDtoId, true);

        when(this.bookingService.removeBooking(fakeBookingDtoId))
                .thenReturn(fakeBookingDto);

        // WHEN
        this.mockMvc.perform(delete("/bookings/{id}", fakeBookingDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode bookingDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeBookingDto, bookingDtoJsonNode);
                });

        verify(bookingService).removeBooking(fakeBookingDtoId);
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

                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("get-booking-by-id-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("all-bookings-GET")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("add-booking-POST")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("update-booking-by-id-with-body-PUT")),
                () -> assertNotNull(bookingDtoJsonNode.path("_links").get("remove-booking-by-id-DELETE"))
        );
    }
}