package com.tascigorkem.flightbookingservice.controller.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.service.flight.AirlineService;
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
@RequestMapping("/airlines")
public class AirlineController {

    private final AirlineService airlineService;
    private final PagedResourcesAssembler<AirlineDto> pagedResourcesAssembler;

    protected static EntityModel<AirlineDto> toEntityModelWithLinks(AirlineDto airlineDto) {
        EntityModel<AirlineDto> airlineDtoEntityModel = EntityModel.of(airlineDto);
        setLinks(airlineDtoEntityModel);
        return airlineDtoEntityModel;
    }

    protected static void setLinks(EntityModel<AirlineDto> airlineDtoEntityModel) {
        AirlineDto airlineDto = Objects.requireNonNull(airlineDtoEntityModel.getContent());
        airlineDtoEntityModel.add(linkTo(methodOn(AirlineController.class).getAirlineById(airlineDto.getId())).withRel("get-airline-by-id-GET"));
        airlineDtoEntityModel.add(linkTo(AirlineController.class).withRel("all-airlines-GET"));
        airlineDtoEntityModel.add(linkTo(AirlineController.class).withRel("add-airline-POST"));
        airlineDtoEntityModel.add(linkTo(methodOn(AirlineController.class).updateAirline(airlineDto.getId(), airlineDto)).withRel("update-airline-by-id-with-body-PUT"));
        airlineDtoEntityModel.add(linkTo(methodOn(AirlineController.class).removeAirline(airlineDto.getId())).withRel("remove-airline-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/airlines"
     *
     * @return retrieve all non-deleted airlines
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirlineDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<AirlineDto>>> getAllAirlines(Pageable pageable) {
        PagedModel<EntityModel<AirlineDto>> pagedModel = pagedResourcesAssembler.toModel(
                airlineService.getAllAirlines(pageable));
        pagedModel.getContent().forEach(AirlineController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/airlines/{id}"
     *
     * @param id of the airline to be retrieved
     * @return airline
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirlineDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirlineDto>> getAirlineById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(airlineService.getAirlineById(id))
        );
    }

    /**
     * Handles the incoming POST request "/airlines"
     *
     * @param airlineDto fields of airline to be added
     * @return retrieve added airline
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirlineDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirlineDto>> addAirline(@Valid @RequestBody AirlineDto airlineDto) {
        AirlineDto addedAirline = airlineService.addAirline(airlineDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedAirline.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedAirline));
    }

    /**
     * Handles the incoming PUT request "/airlines/{id}"
     *
     * @param id of the airline to be updated
     * @return updated airline
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirlineDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirlineDto>> updateAirline(@PathVariable("id") UUID id, @RequestBody AirlineDto airlineDto) {
        airlineDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(airlineService.updateAirline(airlineDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/airlines/{id}"
     *
     * @param id of the airline to be removed
     * @return removed airline
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirlineDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirlineDto>> removeAirline(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(airlineService.removeAirline(id))
        );
    }

}
