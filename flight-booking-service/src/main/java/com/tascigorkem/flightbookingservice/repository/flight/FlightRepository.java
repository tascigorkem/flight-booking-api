package com.tascigorkem.flightbookingservice.repository.flight;

import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, UUID> {

    Page<FlightEntity> findAllByDeletionTimeIsNull(Pageable pageable);
}
