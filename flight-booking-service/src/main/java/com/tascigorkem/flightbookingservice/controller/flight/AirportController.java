package com.tascigorkem.flightbookingservice.controller.flight;

import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.service.flight.AirportService;
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
@RequestMapping("/airports")
public class AirportController {

    private final AirportService airportService;
    private final PagedResourcesAssembler<AirportDto> pagedResourcesAssembler;

    protected static EntityModel<AirportDto> toEntityModelWithLinks(AirportDto airportDto) {
        EntityModel<AirportDto> airportDtoEntityModel = EntityModel.of(airportDto);
        setLinks(airportDtoEntityModel);
        return airportDtoEntityModel;
    }

    protected static void setLinks(EntityModel<AirportDto> airportDtoEntityModel) {
        AirportDto airportDto = Objects.requireNonNull(airportDtoEntityModel.getContent());
        airportDtoEntityModel.add(linkTo(methodOn(AirportController.class).getAirportById(airportDto.getId())).withRel("get-airport-by-id-GET"));
        airportDtoEntityModel.add(linkTo(AirportController.class).withRel("all-airports-GET"));
        airportDtoEntityModel.add(linkTo(AirportController.class).withRel("add-airport-POST"));
        airportDtoEntityModel.add(linkTo(methodOn(AirportController.class).updateAirport(airportDto.getId(), airportDto)).withRel("update-airport-by-id-with-body-PUT"));
        airportDtoEntityModel.add(linkTo(methodOn(AirportController.class).removeAirport(airportDto.getId())).withRel("remove-airport-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/airports"
     *
     * @return retrieve all non-deleted airports
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirportDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<AirportDto>>> getAllAirports(Pageable pageable) {
        PagedModel<EntityModel<AirportDto>> pagedModel = pagedResourcesAssembler.toModel(
                airportService.getAllAirports(pageable));
        pagedModel.getContent().forEach(AirportController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/airports/{id}"
     *
     * @param id of the airport to be retrieved
     * @return airport
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirportDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirportDto>> getAirportById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(airportService.getAirportById(id))
        );
    }

    /**
     * Handles the incoming POST request "/airports"
     *
     * @param airportDto fields of airport to be added
     * @return retrieve added airport
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirportDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirportDto>> addAirport(@Valid @RequestBody AirportDto airportDto) {
        AirportDto addedAirport = airportService.addAirport(airportDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedAirport.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedAirport));
    }

    /**
     * Handles the incoming PUT request "/airports/{id}"
     *
     * @param id of the airport to be updated
     * @return updated airport
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirportDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirportDto>> updateAirport(@PathVariable("id") UUID id, @RequestBody AirportDto airportDto) {
        airportDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(airportService.updateAirport(airportDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/airports/{id}"
     *
     * @param id of the airport to be removed
     * @return removed airport
     * @see com.tascigorkem.flightbookingservice.dto.flight.AirportDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<AirportDto>> removeAirport(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(airportService.removeAirport(id))
        );
    }

}
