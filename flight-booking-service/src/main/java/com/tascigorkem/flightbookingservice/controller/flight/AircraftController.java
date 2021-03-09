package com.tascigorkem.flightbookingservice.controller.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.service.flight.AircraftService;
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
@RequestMapping("/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;
    private final PagedResourcesAssembler<AircraftDto> pagedResourcesAssembler;

    protected static EntityModel<AircraftDto> toEntityModelWithLinks(AircraftDto aircraftDto) {
        EntityModel<AircraftDto> aircraftDtoEntityModel = EntityModel.of(aircraftDto);
        setLinks(aircraftDtoEntityModel);
        return aircraftDtoEntityModel;
    }

    protected static void setLinks(EntityModel<AircraftDto> aircraftDtoEntityModel) {
        AircraftDto aircraftDto = Objects.requireNonNull(aircraftDtoEntityModel.getContent());
        aircraftDtoEntityModel.add(linkTo(methodOn(AircraftController.class).getAircraftById(aircraftDto.getId())).withRel("get-aircraft-by-id-GET"));
        aircraftDtoEntityModel.add(linkTo(AircraftController.class).withRel("all-aircrafts-GET"));
        aircraftDtoEntityModel.add(linkTo(AircraftController.class).withRel("add-aircraft-POST"));
        aircraftDtoEntityModel.add(linkTo(methodOn(AircraftController.class).updateAircraft(aircraftDto.getId(), aircraftDto)).withRel("update-aircraft-by-id-with-body-PUT"));
        aircraftDtoEntityModel.add(linkTo(methodOn(AircraftController.class).removeAircraft(aircraftDto.getId())).withRel("remove-aircraft-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/aircrafts"
     *
     * @return retrieve all non-deleted aircrafts
     * @see com.tascigorkem.flightbookingservice.dto.flight.AircraftDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<AircraftDto>>> getAllAircrafts(Pageable pageable) {
        PagedModel<EntityModel<AircraftDto>> pagedModel = pagedResourcesAssembler.toModel(
                aircraftService.getAllAircrafts(pageable));
        pagedModel.getContent().forEach(AircraftController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/aircrafts/{id}"
     *
     * @param id of the aircraft to be retrieved
     * @return aircraft
     * @see com.tascigorkem.flightbookingservice.dto.flight.AircraftDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AircraftDto>> getAircraftById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(aircraftService.getAircraftById(id))
        );
    }

    /**
     * Handles the incoming POST request "/aircrafts"
     *
     * @param aircraftDto fields of aircraft to be added
     * @return retrieve added aircraft
     * @see com.tascigorkem.flightbookingservice.dto.flight.AircraftDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AircraftDto>> addAircraft(@Valid @RequestBody AircraftDto aircraftDto) {
        AircraftDto addedAircraft = aircraftService.addAircraft(aircraftDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedAircraft.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedAircraft));
    }

    /**
     * Handles the incoming PUT request "/aircrafts/{id}"
     *
     * @param id of the aircraft to be updated
     * @return updated aircraft
     * @see com.tascigorkem.flightbookingservice.dto.flight.AircraftDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AircraftDto>> updateAircraft(@PathVariable("id") UUID id, @RequestBody AircraftDto aircraftDto) {
        aircraftDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(aircraftService.updateAircraft(aircraftDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/aircrafts/{id}"
     *
     * @param id of the aircraft to be removed
     * @return removed aircraft
     * @see com.tascigorkem.flightbookingservice.dto.flight.AircraftDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AircraftDto>> removeAircraft(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(aircraftService.removeAircraft(id))
        );
    }

}
