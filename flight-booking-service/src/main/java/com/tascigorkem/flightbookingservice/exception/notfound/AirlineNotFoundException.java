package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class AirlineNotFoundException extends BaseResourceNotFoundException {

    public AirlineNotFoundException(String keyName, String keyValue) {
        super("Airline", keyName, keyValue);
    }
}
