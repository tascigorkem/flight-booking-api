package com.tascigorkem.flightbookingservice.repository.customer;

import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CustomerRepositoryIT {

    private final CustomerRepository customerRepository;

    @Autowired
    CustomerRepositoryIT(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Test
    void testFindAllByDeletionTimeIsNull() {
        // arrange
        UUID fakeCustomerId1 = EntityModelFaker.fakeId();
        UUID fakeCustomerId2 = EntityModelFaker.fakeId();

        CustomerEntity fakeCustomerEntity1 = EntityModelFaker.getFakeCustomerEntity(fakeCustomerId1, false);
        CustomerEntity fakeCustomerEntity2 = EntityModelFaker.getFakeCustomerEntity(fakeCustomerId2, false);

        fakeCustomerEntity2.setDeletionTime(LocalDateTime.now());

        List<CustomerEntity> customerEntities = Arrays.asList(fakeCustomerEntity1, fakeCustomerEntity2);

        // prepare db; delete all elements and insert entities
        customerRepository.deleteAll();
        customerRepository.saveAll(customerEntities);

        // act
        Pageable pageable = PageRequest.of(0,50);
        Page<CustomerEntity> resultCustomersEntitiesPage = customerRepository.findAllByDeletionTimeIsNull(pageable);

        // assert
        assertNotNull(resultCustomersEntitiesPage.getContent());
        List<CustomerEntity> resultCustomersEntities = resultCustomersEntitiesPage.getContent();

        Optional<CustomerEntity> optCustomerEntity1 = resultCustomersEntities.stream().filter(customerEntity -> customerEntity.getId().equals(fakeCustomerId1)).findAny();
        Optional<CustomerEntity> optCustomerEntity2 = resultCustomersEntities.stream().filter(customerEntity -> customerEntity.getId().equals(fakeCustomerId2)).findAny();

        assertTrue(optCustomerEntity1.isPresent());
        assertFalse(optCustomerEntity2.isPresent());

        CustomerEntity resultCustomerEntity1 = optCustomerEntity1.get();

        assertAll(
                () -> assertEquals(fakeCustomerEntity1.getId(), resultCustomerEntity1.getId()),
                () -> assertEquals(fakeCustomerEntity1.getName(), resultCustomerEntity1.getName()),
                () -> assertEquals(fakeCustomerEntity1.getSurname(), resultCustomerEntity1.getSurname()),
                () -> assertEquals(fakeCustomerEntity1.getEmail(), resultCustomerEntity1.getEmail()),
                () -> assertEquals(fakeCustomerEntity1.getPassword(), resultCustomerEntity1.getPassword()),
                () -> assertEquals(fakeCustomerEntity1.getPhone(), resultCustomerEntity1.getPhone()),
                () -> assertEquals(fakeCustomerEntity1.getAge(), resultCustomerEntity1.getAge()),
                () -> assertEquals(fakeCustomerEntity1.getCity(), resultCustomerEntity1.getCity()),
                () -> assertEquals(fakeCustomerEntity1.getCountry(), resultCustomerEntity1.getCountry()),

                () -> assertNotNull(resultCustomerEntity1.getCreationTime()),
                () -> assertNotNull(resultCustomerEntity1.getUpdateTime()),
                () -> assertNull(resultCustomerEntity1.getDeletionTime())
        );
    }
}