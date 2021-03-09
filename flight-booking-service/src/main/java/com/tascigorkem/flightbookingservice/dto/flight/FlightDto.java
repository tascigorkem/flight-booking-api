package com.tascigorkem.flightbookingservice.dto.flight;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class FlightDto extends BaseDto {

    @NotNull(message = "departureDate cannot be null")
    private LocalDateTime departureDate;

    @NotNull(message = "arrivalDate cannot be null")
    private LocalDateTime arrivalDate;

    @NotNull(message = "price cannot be null")
    private BigDecimal price;

    private AirportDto departureAirport;
    private AirportDto destinationAirport;
    private AircraftDto aircraft;
    private AirlineDto airline;

}
