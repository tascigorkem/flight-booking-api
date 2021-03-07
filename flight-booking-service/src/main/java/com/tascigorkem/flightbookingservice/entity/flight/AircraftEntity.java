package com.tascigorkem.flightbookingservice.entity.flight;

import com.tascigorkem.flightbookingservice.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "aircraft")
public class AircraftEntity extends BaseEntity {

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "code")
    private String code;

    @Column(name = "seat")
    private short seat;

    @Column(name = "country")
    private String country;

    @Column(name = "manufacture_date")
    private LocalDateTime manufacturerDate;

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightEntity> flights;
}
