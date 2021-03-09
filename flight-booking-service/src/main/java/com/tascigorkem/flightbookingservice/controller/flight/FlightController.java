package com.tascigorkem.flightbookingservice.controller.flight;

import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.service.flight.FlightService;
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
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final PagedResourcesAssembler<FlightDto> pagedResourcesAssembler;

    protected static EntityModel<FlightDto> toEntityModelWithLinks(FlightDto flightDto) {
        EntityModel<FlightDto> flightDtoEntityModel = EntityModel.of(flightDto);
        setLinks(flightDtoEntityModel);
        return flightDtoEntityModel;
    }

    protected static void setLinks(EntityModel<FlightDto> flightDtoEntityModel) {
        FlightDto flightDto = Objects.requireNonNull(flightDtoEntityModel.getContent());
        flightDtoEntityModel.add(linkTo(methodOn(FlightController.class).getFlightById(flightDto.getId())).withRel("get-flight-by-id-GET"));
        flightDtoEntityModel.add(linkTo(FlightController.class).withRel("all-flights-GET"));
        flightDtoEntityModel.add(linkTo(FlightController.class).withRel("add-flight-POST"));
        flightDtoEntityModel.add(linkTo(methodOn(FlightController.class).updateFlight(flightDto.getId(), flightDto)).withRel("update-flight-by-id-with-body-PUT"));
        flightDtoEntityModel.add(linkTo(methodOn(FlightController.class).removeFlight(flightDto.getId())).withRel("remove-flight-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/flights"
     *
     * @return retrieve all non-deleted flights
     * @see com.tascigorkem.flightbookingservice.dto.flight.FlightDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<FlightDto>>> getAllFlights(Pageable pageable) {
        PagedModel<EntityModel<FlightDto>> pagedModel = pagedResourcesAssembler.toModel(
                flightService.getAllFlights(pageable));
        pagedModel.getContent().forEach(FlightController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/flights/{id}"
     *
     * @param id of the flight to be retrieved
     * @return flight
     * @see com.tascigorkem.flightbookingservice.dto.flight.FlightDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<FlightDto>> getFlightById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(flightService.getFlightById(id))
        );
    }

    /**
     * Handles the incoming POST request "/flights"
     *
     * @param flightDto fields of flight to be added
     * @return retrieve added flight
     * @see com.tascigorkem.flightbookingservice.dto.flight.FlightDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<FlightDto>> addFlight(@Valid @RequestBody FlightDto flightDto) {
        FlightDto addedFlight = flightService.addFlight(flightDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedFlight.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedFlight));
    }

    /**
     * Handles the incoming PUT request "/flights/{id}"
     *
     * @param id of the flight to be updated
     * @return updated flight
     * @see com.tascigorkem.flightbookingservice.dto.flight.FlightDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<FlightDto>> updateFlight(@PathVariable("id") UUID id, @RequestBody FlightDto flightDto) {
        flightDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(flightService.updateFlight(flightDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/flights/{id}"
     *
     * @param id of the flight to be removed
     * @return removed flight
     * @see com.tascigorkem.flightbookingservice.dto.flight.FlightDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<FlightDto>> removeFlight(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(flightService.removeFlight(id))
        );
    }

}
