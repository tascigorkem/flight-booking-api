package com.tascigorkem.flightbookingservice.service.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.faker.DtoModelFaker;
import com.tascigorkem.flightbookingservice.faker.EntityModelFaker;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.customer.CustomerMapper.CUSTOMER_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerService subject = new CustomerServiceImpl(customerRepository);

    /**
     * Unit test for CustomerService:getAllCustomers
     */
    @Test
    void getAllCustomers_RetrieveCustomers_ShouldReturnNotDeletedCustomers() {
        // GIVEN
        List<CustomerEntity> fakeCustomerEntityList = Arrays.asList(
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true),
                EntityModelFaker.getFakeCustomerEntity(EntityModelFaker.fakeId(), true)
        );

        List<CustomerDto> fakeCustomerDtoList = CUSTOMER_MAPPER.toCustomerDtoList(fakeCustomerEntityList);

        Pageable pageable = PageRequest.of(0, 5);
        Page<CustomerEntity> fakePageCustomerEntity = new PageImpl<>(fakeCustomerEntityList, pageable, fakeCustomerEntityList.size());

        when(customerRepository.findAllByDeletionTimeIsNull(pageable)).thenReturn(fakePageCustomerEntity);

        // WHEN
        Page<CustomerDto> result = subject.getAllCustomers(pageable);

        // THEN
        assertEquals(fakeCustomerDtoList, result.toList());
        verify(customerRepository).findAllByDeletionTimeIsNull(pageable);
    }

    /**
     * Unit test for CustomerService:getCustomerById
     */
    @Test
    void getCustomerById_WithCustomerId_ShouldReturnCustomer() {
        // GIVEN
        UUID fakeCustomerId = EntityModelFaker.fakeId();
        CustomerEntity fakeCustomerEntity = EntityModelFaker.getFakeCustomerEntity(fakeCustomerId, true);
        CustomerDto expectedCustomerDto = CUSTOMER_MAPPER.toCustomerDto(fakeCustomerEntity);

        when(customerRepository.findById(fakeCustomerId)).thenReturn(Optional.of(fakeCustomerEntity));

        // WHEN
        CustomerDto result = subject.getCustomerById(fakeCustomerId);

        // THEN
        assertEquals(expectedCustomerDto, result);
        verify(customerRepository).findById(fakeCustomerId);
    }

    /**
     * Unit test for CustomerService:addCustomer
     */
    @Test
    void addCustomer_SaveIntoDatabase_ShouldSaveAndReturnSavedCustomer() {
        // GIVEN
        UUID fakeCustomerId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerId, true);
        CustomerEntity fakeCustomerEntity = CUSTOMER_MAPPER.toCustomerEntity(fakeCustomerDto);
        CustomerDto expectedCustomerDto = CUSTOMER_MAPPER.toCustomerDto(fakeCustomerEntity);

        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(fakeCustomerEntity);

        // WHEN
        CustomerDto result = subject.addCustomer(fakeCustomerDto);

        // THEN
        assertEquals(expectedCustomerDto, result);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    /**
     * Unit test for CustomerService:updateCustomer
     */
    @Test
    void updateCustomer_SaveIntoDatabase_ShouldSaveAndReturnSavedCustomer() {
        // GIVEN
        UUID fakeCustomerId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerId, true);
        CustomerEntity fakeCustomerEntity = CUSTOMER_MAPPER.toCustomerEntity(fakeCustomerDto);
        CustomerDto expectedCustomerDto = CUSTOMER_MAPPER.toCustomerDto(fakeCustomerEntity);

        when(customerRepository.findById(fakeCustomerId)).thenReturn(Optional.of(fakeCustomerEntity));
        when(customerRepository.save(fakeCustomerEntity)).thenReturn(fakeCustomerEntity);

        // WHEN
        CustomerDto result = subject.updateCustomer(fakeCustomerDto);

        // THEN
        assertEquals(expectedCustomerDto, result);
        verify(customerRepository).findById(fakeCustomerId);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    /**
     * Unit test for CustomerService:removeCustomer
     */
    @Test
    void removeCustomer_SetStatusDAndSaveIntoDatabase_ShouldSaveAndReturnRemovedCustomer() {
        // GIVEN
        UUID fakeCustomerId = DtoModelFaker.fakeId();
        CustomerDto fakeCustomerDto = DtoModelFaker.getFakeCustomerDto(fakeCustomerId, true);
        CustomerEntity fakeCustomerEntity = CUSTOMER_MAPPER.toCustomerEntity(fakeCustomerDto);
        CustomerDto expectedCustomerDto = CUSTOMER_MAPPER.toCustomerDto(fakeCustomerEntity);

        when(customerRepository.findById(fakeCustomerId)).thenReturn(Optional.of(fakeCustomerEntity));
        when(customerRepository.save(fakeCustomerEntity)).thenReturn(fakeCustomerEntity);

        // WHEN
        CustomerDto result = subject.removeCustomer(fakeCustomerId);

        // THEN
        assertEquals(expectedCustomerDto, result);
        verify(customerRepository).findById(fakeCustomerId);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

}
