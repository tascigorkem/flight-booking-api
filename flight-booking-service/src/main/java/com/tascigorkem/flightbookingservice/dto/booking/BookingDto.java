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

    @NotBlank(message = "paymentDate cannot be blank")
    private LocalDateTime paymentDate;

    @NotBlank(message = "paymentAmount cannot be blank")
    private BigDecimal paymentAmount;

    @NotBlank(message = "insurance cannot be blank")
    private boolean insurance;

    @NotBlank(message = "luggage cannot be blank")
    private short luggage;

    private CustomerDto customer;
    private FlightDto flight;
}
