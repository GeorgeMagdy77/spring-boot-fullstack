package com.example.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicteResourceException extends RuntimeException{
    public DuplicteResourceException(String message) {
        super(message);
    }
}
