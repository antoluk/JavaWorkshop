package com.example.demo.exeptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OutOfboundExp extends RuntimeException
{
    public OutOfboundExp(String message) {
        super(message);
    }
}
