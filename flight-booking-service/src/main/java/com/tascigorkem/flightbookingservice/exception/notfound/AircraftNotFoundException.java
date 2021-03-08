package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class AircraftNotFoundException extends BaseResourceNotFoundException {

    public AircraftNotFoundException(String keyName, String keyValue) {
        super("Aircraft", keyName, keyValue);
    }
}
