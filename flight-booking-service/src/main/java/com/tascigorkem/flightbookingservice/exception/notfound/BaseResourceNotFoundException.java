package com.tascigorkem.flightbookingservice.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BaseResourceNotFoundException extends RuntimeException {

    BaseResourceNotFoundException(String resourceType, String keyName, String keyValue) {
        super(resourceType + " with " + keyName + "[" + keyValue + "] not found.");
    }
}