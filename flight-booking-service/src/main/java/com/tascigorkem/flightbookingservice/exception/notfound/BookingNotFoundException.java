package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class BookingNotFoundException extends BaseResourceNotFoundException {

    public BookingNotFoundException(String keyName, String keyValue) {
        super("Booking", keyName, keyValue);
    }
}
