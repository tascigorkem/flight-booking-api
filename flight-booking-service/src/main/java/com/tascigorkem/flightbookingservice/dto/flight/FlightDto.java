package com.tascigorkem.flightbookingservice.dto.flight;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class FlightDto extends BaseDto {

    @NotBlank(message = "departureDate cannot be blank")
    private LocalDateTime departureDate;

    @NotBlank(message = "arrivalDate cannot be blank")
    private LocalDateTime arrivalDate;

    @NotBlank(message = "price cannot be blank")
    private BigDecimal price;

    @NotBlank(message = "departureAirport cannot be blank")
    private AirportDto departureAirport;

    @NotBlank(message = "destinationAirport cannot be blank")
    private AirportDto destinationAirport;

    @NotBlank(message = "aircraft cannot be blank")
    private AircraftDto aircraft;

    @NotBlank(message = "airline cannot be blank")
    private AirlineDto airline;

}
