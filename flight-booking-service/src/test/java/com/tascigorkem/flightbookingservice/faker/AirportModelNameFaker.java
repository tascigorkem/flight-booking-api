package com.tascigorkem.flightbookingservice.faker;

import java.util.HashMap;
import java.util.Map;

public enum AirportModelNameFaker {
    BEIJING("BJS"),
    BUCHAREST("BUH"),
    BUENOS_AIRES("BUE"),
    CHICAGO("CHI"),
    JAKARTA("JKT"),
    ISTANBUL ("IST"),
    LONDON("LON"),
    MILAN("MIL"),
    MONTREAL("YMQ"),
    MOSCOW("MOW"),
    NEW_YORK_CITY("NYC"),
    OSAKA("OSA"),
    PARIS("PAR"),
    RIO_DE_JANEIRO("RIO"),
    ROME("ROM"),
    SAO_PAULO("SAO"),
    SEOUL("SEL"),
    STOCKHOLM("STO"),
    TOKYO("TYO"),
    TORONTO("YTO"),
    WASHINGTON_DC("WAS");

    private static final Map<String, AirportModelNameFaker> airportMap = new HashMap<>();

    static {
        for (AirportModelNameFaker airport : values()) {
            airportMap.put(airport.code, airport);
        }
    }

    public final String code;

    AirportModelNameFaker(String code) {
        this.code = code;
    }

    public static AirportModelNameFaker valueForCode(String code) {
        return airportMap.get(code);
    }
}
