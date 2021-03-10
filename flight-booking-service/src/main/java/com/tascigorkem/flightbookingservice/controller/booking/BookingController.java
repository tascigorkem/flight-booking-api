package com.tascigorkem.flightbookingservice.controller.booking;

import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
// hateoas - application/hal+json (hypertext application language)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final PagedResourcesAssembler<BookingDto> pagedResourcesAssembler;

    protected static EntityModel<BookingDto> toEntityModelWithLinks(BookingDto bookingDto) {
        EntityModel<BookingDto> bookingDtoEntityModel = EntityModel.of(bookingDto);
        setLinks(bookingDtoEntityModel);
        return bookingDtoEntityModel;
    }

    protected static void setLinks(EntityModel<BookingDto> bookingDtoEntityModel) {
        BookingDto bookingDto = Objects.requireNonNull(bookingDtoEntityModel.getContent());
        bookingDtoEntityModel.add(linkTo(methodOn(BookingController.class).getBookingById(bookingDto.getId())).withRel("get-booking-by-id-GET"));
        bookingDtoEntityModel.add(linkTo(BookingController.class).withRel("all-bookings-GET"));
        bookingDtoEntityModel.add(linkTo(BookingController.class).withRel("add-booking-POST"));
        bookingDtoEntityModel.add(linkTo(methodOn(BookingController.class).updateBooking(bookingDto.getId(), bookingDto)).withRel("update-booking-by-id-with-body-PUT"));
        bookingDtoEntityModel.add(linkTo(methodOn(BookingController.class).removeBooking(bookingDto.getId())).withRel("remove-booking-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/bookings"
     *
     * @return retrieve all non-deleted bookings
     * @see com.tascigorkem.flightbookingservice.dto.booking.BookingDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<BookingDto>>> getAllBookings(Pageable pageable) {
        PagedModel<EntityModel<BookingDto>> pagedModel = pagedResourcesAssembler.toModel(
                bookingService.getAllBookings(pageable));
        pagedModel.getContent().forEach(BookingController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/bookings/{id}"
     *
     * @param id of the booking to be retrieved
     * @return booking
     * @see com.tascigorkem.flightbookingservice.dto.booking.BookingDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BookingDto>> getBookingById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(bookingService.getBookingById(id))
        );
    }

    /**
     * Handles the incoming POST request "/bookings"
     *
     * @param bookingDto fields of booking to be added
     * @return retrieve added booking
     * @see com.tascigorkem.flightbookingservice.dto.booking.BookingDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BookingDto>> addBooking(@Valid @RequestBody BookingDto bookingDto) {
        BookingDto addedBooking = bookingService.addBooking(bookingDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedBooking.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedBooking));
    }

    /**
     * Handles the incoming PUT request "/bookings/{id}"
     *
     * @param id of the booking to be updated
     * @return updated booking
     * @see com.tascigorkem.flightbookingservice.dto.booking.BookingDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BookingDto>> updateBooking(@PathVariable("id") UUID id, @RequestBody BookingDto bookingDto) {
        bookingDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(bookingService.updateBooking(bookingDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/bookings/{id}"
     *
     * @param id of the booking to be removed
     * @return removed booking
     * @see com.tascigorkem.flightbookingservice.dto.booking.BookingDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BookingDto>> removeBooking(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(bookingService.removeBooking(id))
        );
    }

}
