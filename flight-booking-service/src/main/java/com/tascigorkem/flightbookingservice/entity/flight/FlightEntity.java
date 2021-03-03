package com.tascigorkem.flightbookingservice.entity.flight;

import com.tascigorkem.flightbookingservice.entity.base.BaseEntity;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "flight")
public class FlightEntity extends BaseEntity {

    @Column(name = "departure_date")
    private LocalDateTime departureDate;

    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_airport_id")
    private AirportEntity departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dest_airport_id")
    private AirportEntity destinationAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private AircraftEntity aircraft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    private AirlineEntity airline;

    @Column(name = "price")
    private BigDecimal price;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingEntity> bookings;
}
