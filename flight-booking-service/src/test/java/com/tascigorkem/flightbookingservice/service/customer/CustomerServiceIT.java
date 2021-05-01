package com.tascigorkem.flightbookingservice.service.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.customer.CustomerMapper.CUSTOMER_MAPPER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CustomerServiceIT {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @Autowired
    CustomerServiceIT(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    /**
     * Integration test for CustomerService:getAllCustomers
     * Checking whether connection with CustomerRepository is successful
     */
    @Test
    void getAllCustomers_RetrieveCustomers_ShouldReturnNotDeletedCustomers() {
        // GIVEN
        UUID fakeCustomerId1 = EntityModelFaker.fakeId();
        UUID fakeCustomerId2 = EntityModelFaker.fakeId();

        List<CustomerEntity> fakeCustomerEntities = new ArrayList<>();
        fakeCustomerEntities.add(EntityModelFaker.getFakeCustomerEntity(fakeCustomerId1, true));
        fakeCustomerEntities.add(EntityModelFaker.getFakeCustomerEntity(fakeCustomerId2, true));

        List<CustomerDto> expectedCustomerDtos = CUSTOMER_MAPPER.toCustomerDtoList(fakeCustomerEntities);
        CustomerDto expectedCustomerDto1 = expectedCustomerDtos.get(0);
        CustomerDto expectedCustomerDto2 = expectedCustomerDtos.get(1);

        // prepare db, insert entities
        customerRepository.deleteAll();
        customerRepository.saveAll(fakeCustomerEntities);

        Pageable pageable = PageRequest.of(0, 5);
        // WHEN

        Page<CustomerDto> resultCustomerDtoPage = customerService.getAllCustomers(pageable);

        // THEN
        assertNotNull(resultCustomerDtoPage.getContent());
        List<CustomerDto> resultCustomerDtoList = resultCustomerDtoPage.getContent();

        Optional<CustomerDto> optionalResultCustomerDto1 = resultCustomerDtoList.stream()
                .filter(customerDtoItem -> fakeCustomerId1.equals(customerDtoItem.getId())).findAny();

        Optional<CustomerDto> optionalResultCustomerDto2 = resultCustomerDtoList.stream()
                .filter(customerDtoItem -> fakeCustomerId2.equals(customerDtoItem.getId())).findAny();

        assertTrue(optionalResultCustomerDto1.isPresent());
        assertTrue(optionalResultCustomerDto2.isPresent());

        CustomerDto resultCustomerDto1 = optionalResultCustomerDto1.get();
        CustomerDto resultCustomerDto2 = optionalResultCustomerDto2.get();

        assertAll(
                () -> assertEquals(expectedCustomerDto1.getId(), resultCustomerDto1.getId()),
                () -> assertNotNull(resultCustomerDto1.getCreationTime()),
                () -> assertNotNull(resultCustomerDto1.getUpdateTime()),

                () -> assertEquals(expectedCustomerDto2.getId(), resultCustomerDto2.getId()),
                () -> assertNotNull(resultCustomerDto2.getCreationTime()),
                () -> assertNotNull(resultCustomerDto2.getUpdateTime())
        );
    }
}
