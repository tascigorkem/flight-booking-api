package com.tascigorkem.flightbookingservice.entity.flight;

import com.tascigorkem.flightbookingservice.entity.base.BaseEntity;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "airplane")
public class AirlineEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "county")
    private String county;

    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightEntity> flights;
}
