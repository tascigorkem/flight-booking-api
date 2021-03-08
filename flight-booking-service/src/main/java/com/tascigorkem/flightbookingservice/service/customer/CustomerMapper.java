package com.tascigorkem.flightbookingservice.service.customer;

import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerMapper CUSTOMER_MAPPER = Mappers.getMapper( CustomerMapper.class );

    CustomerDto toCustomerDto(CustomerEntity customerEntity);

    List<CustomerDto> toCustomerDtoList(List<CustomerEntity> customerEntityList);

    CustomerEntity toCustomerEntity(CustomerDto customerDto);

    List<CustomerEntity> toCustomerEntityList(List<CustomerDto> customerDtoList);
}
