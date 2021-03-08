package com.tascigorkem.flightbookingservice.faker;

import com.github.javafaker.Country;
import com.github.javafaker.Faker;
import com.tascigorkem.flightbookingservice.dto.booking.BookingDto;
import com.tascigorkem.flightbookingservice.dto.customer.CustomerDto;
import com.tascigorkem.flightbookingservice.dto.flight.AircraftDto;
import com.tascigorkem.flightbookingservice.dto.flight.AirlineDto;
import com.tascigorkem.flightbookingservice.dto.flight.AirportDto;
import com.tascigorkem.flightbookingservice.dto.flight.FlightDto;
import com.tascigorkem.flightbookingservice.enums.BookingState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DtoModelFaker {
    private static final Faker faker = Faker.instance();

    private DtoModelFaker() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID fakeId() {
        return UUID.randomUUID();
    }

    public static BookingDto getFakeBookingDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return BookingDto.builder()
                .id(id)
                .state(EnumRandomizeUtil.randomEnum(BookingState.class).name())
                .paymentDate(LocalDateTime.now())
                .paymentAmount(BigDecimal.valueOf(faker.number().randomDouble(2, 30, 300)))
                .insurance(true)
                .luggage((short) faker.number().numberBetween(30, 300))
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static CustomerDto getFakeCustomerDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        Country country = faker.country();
        return CustomerDto.builder()
                .id(id)
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .phone(faker.phoneNumber().cellPhone())
                .age((short) faker.number().numberBetween(0, 100))
                .city(country.capital())
                .country(country.name())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AircraftDto getFakeAircraftDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return AircraftDto.builder()
                .id(id)
                .modelName(EnumRandomizeUtil.randomEnum(AircraftModelNameFaker.class).name())
                .code(faker.company().suffix())
                .seat((short) faker.number().numberBetween(0, 700))
                .country(faker.country().name())
                .manufacturerDate(LocalDateTime.now())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AirlineDto getFakeAirlineDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return AirlineDto.builder()
                .id(id)
                .name(faker.company().name())
                .country(faker.country().name())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AirportDto getFakeAirportDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        AirportModelNameFaker airport = EnumRandomizeUtil.randomEnum(AirportModelNameFaker.class);
        return AirportDto.builder()
                .id(id)
                .name(faker.name().firstName())
                .code(airport.code)
                .city(airport.name())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static FlightDto getFakeFlightDto(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return FlightDto.builder()
                .id(id)
                .departureDate(LocalDateTime.now())
                .arrivalDate(LocalDateTime.now())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 30, 300)))
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }
}
