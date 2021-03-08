package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class AirportNotFoundException extends BaseResourceNotFoundException {

    public AirportNotFoundException(String keyName, String keyValue) {
        super("Airport", keyName, keyValue);
    }
}
