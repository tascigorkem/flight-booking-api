package com.tascigorkem.flightbookingservice.dto.booking;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BookingDto extends BaseDto {

    @NotBlank(message = "state cannot be blank")
    private String state;

    @NotNull(message = "paymentDate cannot be null")
    private LocalDateTime paymentDate;

    @NotNull(message = "paymentAmount cannot be null")
    private BigDecimal paymentAmount;

    @NotNull(message = "insurance cannot be null")
    private boolean insurance;

    @NotNull(message = "luggage cannot be null")
    private short luggage;

    private CustomerDto customer;
    private FlightDto flight;
}
