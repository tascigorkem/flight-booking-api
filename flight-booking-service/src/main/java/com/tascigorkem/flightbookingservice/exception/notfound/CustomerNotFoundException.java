package com.tascigorkem.flightbookingservice.exception.notfound;

import com.tascigorkem.flightbookingservice.exception.notfound.base.BaseResourceNotFoundException;

public class CustomerNotFoundException extends BaseResourceNotFoundException {

    public CustomerNotFoundException(String keyName, String keyValue) {
        super("Customer", keyName, keyValue);
    }
}
