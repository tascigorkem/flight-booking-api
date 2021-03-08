package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class FlightNotFoundException extends BaseResourceNotFoundException {

    public FlightNotFoundException(String keyName, String keyValue) {
        super("Flight", keyName, keyValue);
    }
}
