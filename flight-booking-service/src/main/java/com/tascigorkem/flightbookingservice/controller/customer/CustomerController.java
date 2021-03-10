package com.tascigorkem.flightbookingservice.controller.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.service.customer.CustomerService;
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
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final PagedResourcesAssembler<CustomerDto> pagedResourcesAssembler;

    protected static EntityModel<CustomerDto> toEntityModelWithLinks(CustomerDto customerDto) {
        EntityModel<CustomerDto> customerDtoEntityModel = EntityModel.of(customerDto);
        setLinks(customerDtoEntityModel);
        return customerDtoEntityModel;
    }

    protected static void setLinks(EntityModel<CustomerDto> customerDtoEntityModel) {
        CustomerDto customerDto = Objects.requireNonNull(customerDtoEntityModel.getContent());
        customerDtoEntityModel.add(linkTo(methodOn(CustomerController.class).getCustomerById(customerDto.getId())).withRel("get-customer-by-id-GET"));
        customerDtoEntityModel.add(linkTo(CustomerController.class).withRel("all-customers-GET"));
        customerDtoEntityModel.add(linkTo(CustomerController.class).withRel("add-customer-POST"));
        customerDtoEntityModel.add(linkTo(methodOn(CustomerController.class).updateCustomer(customerDto.getId(), customerDto)).withRel("update-customer-by-id-with-body-PUT"));
        customerDtoEntityModel.add(linkTo(methodOn(CustomerController.class).removeCustomer(customerDto.getId())).withRel("remove-customer-by-id-DELETE"));
    }

    /**
     * Handles the incoming GET request "/customers"
     *
     * @return retrieve all non-deleted customers
     * @see com.tascigorkem.flightbookingservice.dto.customer.CustomerDto
     */
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<EntityModel<CustomerDto>>> getAllCustomers(Pageable pageable) {
        PagedModel<EntityModel<CustomerDto>> pagedModel = pagedResourcesAssembler.toModel(
                customerService.getAllCustomers(pageable));
        pagedModel.getContent().forEach(CustomerController::setLinks);

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * Handles the incoming GET request "/customers/{id}"
     *
     * @param id of the customer to be retrieved
     * @return customer
     * @see com.tascigorkem.flightbookingservice.dto.customer.CustomerDto
     */
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDto>> getCustomerById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(customerService.getCustomerById(id))
        );
    }

    /**
     * Handles the incoming POST request "/customers"
     *
     * @param customerDto fields of customer to be added
     * @return retrieve added customer
     * @see com.tascigorkem.flightbookingservice.dto.customer.CustomerDto
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDto>> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto addedCustomer = customerService.addCustomer(customerDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(addedCustomer.getId()).toUri();

        return ResponseEntity.created(location).body(toEntityModelWithLinks(addedCustomer));
    }

    /**
     * Handles the incoming PUT request "/customers/{id}"
     *
     * @param id of the customer to be updated
     * @return updated customer
     * @see com.tascigorkem.flightbookingservice.dto.customer.CustomerDto
     */
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDto>> updateCustomer(@PathVariable("id") UUID id, @RequestBody CustomerDto customerDto) {
        customerDto.setId(id);
        return ResponseEntity.ok(
                toEntityModelWithLinks(customerService.updateCustomer(customerDto))
        );
    }

    /**
     * Handles the incoming DELETE request "/customers/{id}"
     *
     * @param id of the customer to be removed
     * @return removed customer
     * @see com.tascigorkem.flightbookingservice.dto.customer.CustomerDto
     */
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CustomerDto>> removeCustomer(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                toEntityModelWithLinks(customerService.removeCustomer(id))
        );
    }

}
