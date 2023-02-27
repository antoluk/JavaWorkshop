package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.example.demo.errors.OutOfboundExp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class IntergalController
{
    private static final Logger logger = LogManager.getLogger(IntergalController.class);
    @GetMapping(value="/integral")
        public ResponseEntity<?> ans(
                       @RequestParam (value = "left") String lefts,
                       @RequestParam (value = "right") String rights) {
        double left, right;
        SinIntegral eq;

        try
        {
            logger.info("start parsing");
            left = Double.parseDouble(lefts);
            right = Double.parseDouble(rights);
        } catch (NumberFormatException exp)
            {
                logger.error("Parsing error");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,exp.getMessage()+" parsing error");
            }

        logger.info("parsing end");
        logger.info("counting integral");

        try
        {
           eq= new SinIntegral(left,right);
        }catch (OutOfboundExp exp)
                {
                    logger.error("Wrong borders");
                    throw new ResponseStatusException(HttpStatus.valueOf(500),exp.getMessage());
                }

        logger.info("GOOD ENDING");
            return ResponseEntity.ok(eq);
        }
}
