
package com.example.demo;

import com.example.demo.errors.OutOfboundExp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class SinIntegralTest {
    SinIntegral eq;

    @Test
    public void testIntegral1() {
        eq = new SinIntegral(-5, 5);
        assertEquals(0, eq.getAns());
        eq = new SinIntegral(-5, 2);
        assertEquals(0.6995, eq.getAns());
    }

    @Test
    public void testIntegral2() {

        Exception exception = assertThrows(OutOfboundExp.class, () -> new SinIntegral(-5, -6));
        assertEquals(OutOfboundExp.class, exception.getClass());
    }

}

