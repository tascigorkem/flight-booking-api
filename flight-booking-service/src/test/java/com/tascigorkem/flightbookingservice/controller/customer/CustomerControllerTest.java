package com.tascigorkem.flightbookingservice.controller.customer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.exception.notfound.CustomerNotFoundException;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.service.customer.CustomerService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    /**
     * Unit test for CustomerController:getAllCustomers
     */
    @Test
    void getAllCustomers_RetrieveCustomers_ShouldReturnNotDeletedCustomers() throws Exception {
        // GIVEN
        List<CustomerDto> fakeCustomerDtoList = Arrays.asList(
                DtoModelFaker.getFakeCustomerDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeCustomerDto(DtoModelFaker.fakeId(), true),
                DtoModelFaker.getFakeCustomerDto(DtoModelFaker.fakeId(), true)
        );

        PageRequest pageable = PageRequest.of(0, 20);
        Page<CustomerDto> fakeCustomerDtoPage = new PageImpl<>(fakeCustomerDtoList, pageable, fakeCustomerDtoList.size());

        when(this.customerService.getAllCustomers(any(Pageable.class)))
                .thenReturn(fakeCustomerDtoPage);

        // WHEN
        this.mockMvc.perform(get("/customers"))
                .andDo(print())

                // THEN
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

        verify(customerService).getAllCustomers(any(Pageable.class));
    }


    /**
     * Unit test for CustomerController:getCustomerById
     */
    @Test
    void getCustomerById_WithCustomerId_ShouldReturnCustomer() throws Exception {
        // GIVEN
        UUID fakeCustomerDtoId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerDtoId, true);

        when(this.customerService.getCustomerById(fakeCustomerDtoId))
                .thenReturn(fakeCustomerDto);

        // WHEN
        this.mockMvc.perform(get("/customers/{id}", fakeCustomerDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode customerDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeCustomerDto, customerDtoJsonNode);
                });

        verify(customerService).getCustomerById(fakeCustomerDtoId);
    }

    /**
     * Unit test for CustomerController:getCustomerById
     * Given wrong Customer id and should return 404 Http Status with CustomerNotFoundException message
     */
    @Test
    void getCustomerById_WrongCustomerId_ShouldReturn404NotFound() throws Exception {
        // GIVEN
        UUID wrongFakeCustomerId = DtoModelFaker.fakeId();
        CustomerNotFoundException expectedCustomerNotFoundException =
                new CustomerNotFoundException("id", wrongFakeCustomerId.toString());

        when(customerService.getCustomerById(wrongFakeCustomerId))
                .thenThrow(expectedCustomerNotFoundException);

        // WHEN
        this.mockMvc.perform(get("/customers/{id}", wrongFakeCustomerId))
                .andDo(print())

                // THEN
                .andExpect(status().isNotFound());

        verify(customerService).getCustomerById(wrongFakeCustomerId);
    }

    /**
     * Unit test for CustomerController:addCustomer
     */
    @Test
    void addCustomer_SaveIntoDatabase_ShouldSaveAndReturnSavedCustomer() throws Exception {
        // GIVEN
        UUID fakeCustomerDtoId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerDtoId, true);

        when(this.customerService.addCustomer(fakeCustomerDto))
                .thenReturn(fakeCustomerDto);

        // WHEN
        this.mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeCustomerDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode customerDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeCustomerDto, customerDtoJsonNode);
                });

        verify(customerService).addCustomer(fakeCustomerDto);
    }

    /**
     * Unit test for CustomerController:addCustomer
     */
    @Test
    void addCustomer_WithMissingFieldValue_ShouldReturn400BadRequest() throws Exception {
        // GIVEN
        UUID fakeCustomerDtoId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerDtoId, true);

        when(this.customerService.addCustomer(fakeCustomerDto))
                .thenReturn(fakeCustomerDto);

        fakeCustomerDto.setName(StringUtils.EMPTY);
        // WHEN
        this.mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeCustomerDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isBadRequest());

        verify(customerService, never()).addCustomer(fakeCustomerDto);
    }

    /**
     * Unit test for CustomerController:updateCustomer
     */
    @Test
    void updateCustomer_SaveIntoDatabase_ShouldSaveAndReturnSavedCustomer() throws Exception {
        // GIVEN
        UUID fakeCustomerDtoId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerDtoId, true);

        when(this.customerService.updateCustomer(fakeCustomerDto))
                .thenReturn(fakeCustomerDto);

        // WHEN
        this.mockMvc.perform(put("/customers/{id}", fakeCustomerDtoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeCustomerDto)))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode customerDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeCustomerDto, customerDtoJsonNode);
                });

        verify(customerService).updateCustomer(fakeCustomerDto);
    }

    /**
     * Unit test for CustomerController:removeCustomer
     */
    @Test
    void removeCustomer_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedCustomer() throws Exception {
        // GIVEN
        UUID fakeCustomerDtoId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerDtoId, true);

        when(this.customerService.removeCustomer(fakeCustomerDtoId))
                .thenReturn(fakeCustomerDto);

        // WHEN
        this.mockMvc.perform(delete("/customers/{id}", fakeCustomerDtoId))
                .andDo(print())

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(result -> {

                    assertNotNull(result.getResponse().getContentAsString());
                    JsonNode customerDtoJsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

                    assertFieldsBetweenDtoAndJson(fakeCustomerDto, customerDtoJsonNode);
                });

        verify(customerService).removeCustomer(fakeCustomerDtoId);
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