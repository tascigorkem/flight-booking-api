package com.tascigorkem.flightbookingservice.repository.booking;

import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    Page<BookingEntity> findAllByDeletionTimeIsNull(Pageable pageable);

}
