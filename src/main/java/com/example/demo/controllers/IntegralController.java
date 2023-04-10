package com.example.demo.controllers;

import com.example.demo.counter.CounterThread;
import com.example.demo.errors.OutOfboundExp;
import com.example.demo.logic.SinIntegral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.demo.cache.Cache;
import org.json.JSONObject;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
public class IntegralController {


    private Cache cache;

    @Autowired
    public void setCache(Cache cache) {
        this.cache = cache;
    }


    private static final Logger logger = LogManager.getLogger(IntegralController.class);

    @RequestMapping(value = "/integral",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> ans(
            @RequestParam(value = "values") List<Double> values) {
        logger.info("Get request");
        CounterThread counter = new CounterThread();
        counter.start();
        logger.info("Parsing");
        if (values.size() % 2 != 0 || values.size() < 2) {
            logger.error("wrong amount of parameters");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<SinIntegral> answers;
        answers = generateAnswers(values);
        logger.info("GOOD ENDING!");
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }

    @RequestMapping(value = "/bulk",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity<?> getBulkAnswers(@RequestBody List<Double> values) {
        logger.info("Post bulk start");
        logger.info("parsing");
        if (values.size() % 2 != 0 || values.size() < 2) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        List<SinIntegral> answers;
        answers = generateAnswers(values);

        JSONObject response = new JSONObject();
        logger.info("Generate JSON");
        response.put("answers", answers);
        response.put("minInput", values.stream().min(Double::compare).orElse(null));
        response.put("avgInp", values.stream().mapToDouble(a -> a).average().orElse(Double.MIN_VALUE));
        response.put("maxInput", values.stream().max(Double::compare).orElse(null));
        response.put("minAns", answers.stream().mapToDouble(SinIntegral::getAns).min().orElse(Double.MIN_VALUE));
        response.put("maxAns", answers.stream().mapToDouble(SinIntegral::getAns).max().orElse(Double.MIN_VALUE));
        response.put("avgAns", answers.stream().mapToDouble(SinIntegral::getAns).average().orElse(Double.MIN_VALUE));
        response.put("amount", answers.size());
        logger.info("GOOD ENDING!");
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    public List<SinIntegral> generateAnswers(List<Double> values) {
        List<List<Double>> pvals = new ArrayList<>();
        for (int i = 0, j = 0; i < values.size(); i += 2, j++) {
            pvals.add(new ArrayList<>());
            pvals.get(j).add(values.get(i));
            pvals.get(j).add(values.get(i + 1));
        }
        logger.info("Counting");
        List<SinIntegral> answers;
        try {
            answers = Stream.concat(
                            pvals.stream()
                                    .filter(value -> cache.contains(value.hashCode()))
                                    .map(value -> {
                                        logger.info("get from cache");
                                        return cache.get(value.hashCode());
                                    }),
                            pvals.stream()
                                    .filter(value -> !cache.contains(value.hashCode()))
                                    .map(value -> {
                                        logger.info("count integral");
                                        SinIntegral eq = new SinIntegral(value.get(0), value.get(1));
                                        cache.put(value.hashCode(), eq);
                                        return eq;
                                    })
                    )
                    .toList();
        } catch (OutOfboundExp exp) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exp.getMessage());
        }
        return answers;
    }
}
