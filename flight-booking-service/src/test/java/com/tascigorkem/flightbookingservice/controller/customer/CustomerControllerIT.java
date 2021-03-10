package com.tascigorkem.flightbookingservice.controller.customer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
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

import java.util.Arrays;
import java.util.List;

import static com.tascigorkem.flightbookingservice.service.customer.CustomerMapper.CUSTOMER_MAPPER;
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
class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerRepository customerRepository;

    /**
     * Integration test for CustomerController:getAllCustomers
     * Checking whether connection with CustomerService is successful
     */
    @Test
    void testGetAllCustomers() throws Exception {
        // arrange
        List<CustomerEntity> fakeCustomerEntityList = Arrays.asList(
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<CustomerEntity> fakeCustomerEntityPage = new PageImpl<>(fakeCustomerEntityList, pageable, fakeCustomerEntityList.size());

        when(this.customerRepository.findAllByDeletionTimeIsNull(any(Pageable.class)))
                .thenReturn(fakeCustomerEntityPage);

        List<CustomerDto> fakeCustomerDtoList = CUSTOMER_MAPPER.toCustomerDtoList(fakeCustomerEntityList);
        PageImpl<CustomerDto> fakeCustomerDtoPage = new PageImpl<>(fakeCustomerDtoList, pageable, fakeCustomerDtoList.size());

        // act
        this.mockMvc.perform(get("/customers"))
                .andDo(print())

                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {
                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode parentJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertAll(
                            () -> assertEquals(parentJsonNode.path("page").get("size").asInt(), fakeCustomerDtoPage.getSize()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalElements").asInt(), fakeCustomerDtoPage.getTotalElements()),
                            () -> assertEquals(parentJsonNode.path("page").get("totalPages").asInt(), fakeCustomerDtoPage.getTotalPages()),
                            () -> assertEquals(parentJsonNode.path("page").get("number").asInt(), fakeCustomerDtoPage.getNumber())
                    );

                    JsonNode customerDtoListJsonNode = parentJsonNode.path("_embedded").path("customerDtoList");
                    assertEquals(customerDtoListJsonNode.size(), fakeCustomerDtoList.size());

                    for (int i = 0; i < customerDtoListJsonNode.size(); i++) {
                        JsonNode customerDtoJsonNode = customerDtoListJsonNode.path(i);
                        CustomerDto fakeCustomerDto = fakeCustomerDtoList.get(i);

                        assertFieldsBetweenDtoAndJson(fakeCustomerDto, customerDtoJsonNode);
                    }
                });

        verify(customerRepository).findAllByDeletionTimeIsNull(any(Pageable.class));
    }

    private void assertFieldsBetweenDtoAndJson(CustomerDto fakeCustomerDto, JsonNode customerDtoJsonNode) {
        assertAll(
                () -> assertEquals(customerDtoJsonNode.path("id").asText(), fakeCustomerDto.getId().toString()),
                () -> assertEquals(customerDtoJsonNode.path("name").asText(), fakeCustomerDto.getName()),
                () -> assertEquals(customerDtoJsonNode.path("surname").asText(), fakeCustomerDto.getSurname()),
                () -> assertEquals(customerDtoJsonNode.path("email").asText(), fakeCustomerDto.getEmail()),
                () -> assertEquals(customerDtoJsonNode.path("password").asText(), fakeCustomerDto.getPassword()),
                () -> assertEquals(customerDtoJsonNode.path("phone").asText(), fakeCustomerDto.getPhone()),
                () -> assertEquals(customerDtoJsonNode.path("age").asInt(), fakeCustomerDto.getAge()),
                () -> assertEquals(customerDtoJsonNode.path("city").asText(), fakeCustomerDto.getCity()),
                () -> assertEquals(customerDtoJsonNode.path("country").asText(), fakeCustomerDto.getCountry()),
                () -> assertEquals(customerDtoJsonNode.path("creationTime").asText().substring(0, 19), fakeCustomerDto.getCreationTime().toString().substring(0, 19)),
                () -> assertEquals(customerDtoJsonNode.path("updateTime").asText().substring(0, 19), fakeCustomerDto.getUpdateTime().toString().substring(0, 19)),

                () -> assertNotNull(customerDtoJsonNode.path("_links").get("get-customer-by-id-GET")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("all-customers-GET")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("add-customer-POST")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("update-customer-by-id-with-body-PUT")),
                () -> assertNotNull(customerDtoJsonNode.path("_links").get("remove-customer-by-id-DELETE"))
        );
    }

}
