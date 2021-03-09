package com.tascigorkem.flightbookingservice.dto.flight;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AircraftDto extends BaseDto {

    @NotBlank(message = "modelName cannot be blank")
    private String modelName;

    @NotBlank(message = "code cannot be blank")
    private String code;

    @NotNull(message = "seat cannot be blank")
    private short seat;

    @NotBlank(message = "country cannot be blank")
    private String country;

    @NotNull(message = "manufacturerDate cannot be null")
    private LocalDateTime manufacturerDate;
}
