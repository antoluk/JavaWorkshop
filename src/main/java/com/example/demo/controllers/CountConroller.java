package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.counter.Counter;


@RestController
public class CountConroller {
    private static final Logger logger = LogManager.getLogger(IntergalController.class);

    @GetMapping(value = "/counter")
    public ResponseEntity<?> getCounter() {
        logger.info("Get counter");
        return new ResponseEntity<>(Counter.getCount(), HttpStatus.OK);
    }
}
