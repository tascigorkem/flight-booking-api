package com.tascigorkem.flightbookingservice.faker;

import com.github.javafaker.Country;
import com.github.javafaker.Faker;
import com.tascigorkem.flightbookingservice.entity.booking.BookingEntity;
import com.tascigorkem.flightbookingservice.entity.customer.CustomerEntity;
import com.tascigorkem.flightbookingservice.entity.flight.AircraftEntity;
import com.tascigorkem.flightbookingservice.entity.flight.AirlineEntity;
import com.tascigorkem.flightbookingservice.entity.flight.AirportEntity;
import com.tascigorkem.flightbookingservice.entity.flight.FlightEntity;
import com.tascigorkem.flightbookingservice.enums.BookingState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EntityModelFaker {
    private static final Faker faker = Faker.instance();

    private EntityModelFaker() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID fakeId() {
        return UUID.randomUUID();
    }

    public static BookingEntity getFakeBookingEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return BookingEntity.builder()
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


    public static CustomerEntity getFakeCustomerEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        Country country = faker.country();
        return CustomerEntity.builder()
                .id(id)
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .phone(faker.phoneNumber().cellPhone())
                .age((short) faker.number().numberBetween(0, 100))
                .city(country.capital().replaceAll("[^A-Za-z\\s]", ""))
                .country(country.name().replaceAll("[^A-Za-z\\s]", ""))
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AircraftEntity getFakeAircraftEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return AircraftEntity.builder()
                .id(id)
                .modelName(EnumRandomizeUtil.randomEnum(AircraftModelNameFaker.class).name())
                .code(faker.company().suffix())
                .seat((short) faker.number().numberBetween(0, 700))
                .country(faker.country().name().replaceAll("[^A-Za-z\\s]", ""))
                .manufacturerDate(LocalDateTime.now())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AirlineEntity getFakeAirlineEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return AirlineEntity.builder()
                .id(id)
                .name(faker.company().name())
                .country(faker.country().name().replaceAll("[^A-Za-z\\s]", ""))
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static AirportEntity getFakeAirportEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        AirportModelNameFaker airport = EnumRandomizeUtil.randomEnum(AirportModelNameFaker.class);
        return AirportEntity.builder()
                .id(id)
                .name(faker.name().firstName())
                .code(airport.code)
                .city(airport.name())
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }


    public static FlightEntity getFakeFlightEntity(UUID id, boolean fillDateTimeFields) {
        LocalDateTime dateTime = fillDateTimeFields ? LocalDateTime.now() : null;
        return FlightEntity.builder()
                .id(id)
                .departureDate(LocalDateTime.now())
                .arrivalDate(LocalDateTime.now())
                .price(BigDecimal.valueOf(faker.number().randomDouble(2, 30, 300)))
                .creationTime(dateTime)
                .updateTime(dateTime)
                .build();
    }
}
