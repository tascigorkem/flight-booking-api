package com.tascigorkem.flightbookingservice.service.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Page<CustomerDto> getAllCustomers(Pageable pageable);

    CustomerDto getCustomerById(UUID id);

    CustomerDto addCustomer(CustomerDto customerDto);

    CustomerDto updateCustomer(CustomerDto customerDto);

    CustomerDto removeCustomer(UUID id);
}
