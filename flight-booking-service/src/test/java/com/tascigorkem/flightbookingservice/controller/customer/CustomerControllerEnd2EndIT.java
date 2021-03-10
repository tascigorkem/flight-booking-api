package com.tascigorkem.flightbookingservice.controller.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * End to End Test for CustomerController.class
 * @see CustomerController
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CustomerControllerEnd2EndIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * E2E test for CustomerController:getAllCustomers
     */
    @Test
    void testGetCustomerById() throws JsonProcessingException {
        // arrange
        UUID fakeCustomerEntityId = EntityModelFaker.fakeId();
        CustomerEntity fakeCustomerEntity = EntityModelFaker.getFakeCustomerEntity(fakeCustomerEntityId, false);
        customerRepository.save(fakeCustomerEntity);

        // act
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "/customers/" + fakeCustomerEntityId, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode customerDtoJsonNode = objectMapper.readTree(response.getBody());

        assertFieldsBetweenDtoAndJson(fakeCustomerEntity, customerDtoJsonNode);

        // clean dummy entities from db
        customerRepository.delete(fakeCustomerEntity);
    }

    private void assertFieldsBetweenDtoAndJson(CustomerEntity fakeCustomerEntity, JsonNode customerDtoJsonNode) {
        assertAll(
                () -> assertEquals(customerDtoJsonNode.path("id").asText(), fakeCustomerEntity.getId().toString()),
                () -> assertEquals(customerDtoJsonNode.path("name").asText(), fakeCustomerEntity.getName()),
                () -> assertEquals(customerDtoJsonNode.path("surname").asText(), fakeCustomerEntity.getSurname()),
                () -> assertEquals(customerDtoJsonNode.path("surname").asText(), fakeCustomerEntity.getSurname()),
                () -> assertEquals(customerDtoJsonNode.path("email").asText(), fakeCustomerEntity.getEmail()),
                () -> assertEquals(customerDtoJsonNode.path("password").asText(), fakeCustomerEntity.getPassword()),
                () -> assertEquals(customerDtoJsonNode.path("phone").asText(), fakeCustomerEntity.getPhone()),
                () -> assertEquals(customerDtoJsonNode.path("age").asInt(), fakeCustomerEntity.getAge()),
                () -> assertEquals(customerDtoJsonNode.path("city").asText(), fakeCustomerEntity.getCity()),
                () -> assertEquals(customerDtoJsonNode.path("country").asText(), fakeCustomerEntity.getCountry()),

                () -> assertNotNull(customerDtoJsonNode.path("_links").get("get-customer-by-id-GET")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("all-customers-GET")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("add-customer-POST")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("update-customer-by-id-with-body-PUT")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("remove-customer-by-id-DELETE"))
        );
    }

}
