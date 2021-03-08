package com.tascigorkem.flightbookingservice.service.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.exception.notfound.CustomerNotFoundException;
import com.tascigorkem.flightbookingservice.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.tascigorkem.flightbookingservice.service.customer.CustomerMapper.CUSTOMER_MAPPER;


@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerRepository.findAllByDeletionTimeIsNull(pageable).map(CUSTOMER_MAPPER::toCustomerDto);
    }

    @Override
    public CustomerDto getCustomerById(UUID id) {
        return CUSTOMER_MAPPER.toCustomerDto(customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("id", id.toString())));
    }

    @Override
    public CustomerDto addCustomer(CustomerDto customerDto) {
        CustomerEntity customerEntity = CUSTOMER_MAPPER.toCustomerEntity(customerDto);
        customerEntity.setId(UUID.randomUUID());
        return CUSTOMER_MAPPER.toCustomerDto(customerRepository.save(customerEntity));
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        CustomerEntity customerEntity = customerRepository.findById(customerDto.getId())
                .orElseThrow(() -> new CustomerNotFoundException("id", customerDto.getId().toString()));

        customerEntity.setName(customerDto.getName());
        customerEntity.setSurname(customerDto.getSurname());
        customerEntity.setEmail(customerDto.getEmail());
        customerEntity.setPassword(customerDto.getPassword());
        customerEntity.setPhone(customerDto.getPhone());
        customerEntity.setAge(customerDto.getAge());
        customerEntity.setCity(customerDto.getCity());
        customerEntity.setCountry(customerDto.getCountry());

        return CUSTOMER_MAPPER.toCustomerDto(customerRepository.save(customerEntity));
    }

    @Override
    public CustomerDto removeCustomer(UUID id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("id", id.toString()));

        customerEntity.setDeletionTime(LocalDateTime.now());

        return CUSTOMER_MAPPER.toCustomerDto(customerRepository.save(customerEntity));
    }
}
