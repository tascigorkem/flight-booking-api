package com.tascigorkem.flightbookingservice.dto.flight;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AirlineDto extends BaseDto {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "country cannot be blank")
    private String country;

}
