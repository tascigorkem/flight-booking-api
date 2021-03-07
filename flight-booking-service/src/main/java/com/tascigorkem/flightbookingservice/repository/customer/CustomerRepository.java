package com.tascigorkem.flightbookingservice.repository.customer;

import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Page<CustomerEntity> findAllByDeletionTimeIsNull(Pageable pageable);

}
