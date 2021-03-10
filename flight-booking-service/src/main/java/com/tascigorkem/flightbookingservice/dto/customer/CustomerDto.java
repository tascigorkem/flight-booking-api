package com.tascigorkem.flightbookingservice.dto.customer;

import com.tascigorkem.flightbookingservice.dto.base.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class CustomerDto extends BaseDto {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "surname cannot be blank")
    private String surname;

    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "phone cannot be blank")
    private String phone;

    @NotNull(message = "age cannot be null")
    private short age;

    @NotBlank(message = "city cannot be blank")
    private String city;

    @NotBlank(message = "country cannot be blank")
    private String country;
}
