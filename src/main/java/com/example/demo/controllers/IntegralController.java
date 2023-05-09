package com.example.demo.controllers;

import com.example.demo.cache.Cache;
import com.example.demo.counter.CounterThread;
import com.example.demo.exeptions.OutOfboundExp;
import com.example.demo.logic.SinIntegral;
import com.example.demo.repo.SinIntegralRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
public class IntegralController {


    private static final Logger LOGGER = LogManager.getLogger(IntegralController.class);
    private Cache cache;
    @Autowired
    private SinIntegralRepository sinIntegralRepository;

    @Autowired
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @RequestMapping(value = "/integral",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> ans(
            @RequestParam(value = "values") List<Double> values) {
        LOGGER.info("Get request");
        CounterThread counter = new CounterThread();
        counter.start();
        if (values.size() % 2 != 0 || values.size() < 2) {
            LOGGER.error("wrong amount of parameters");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<List<Double>> parsedValues;
        try {
            parsedValues = parseInput(values);
        }catch (OutOfboundExp exp)
        {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Long> ids = new ArrayList<>();
        for (List<Double> parsedValue : parsedValues) {
            ids.add((long) parsedValue.hashCode());
        }
            CompletableFuture<List<SinIntegral>> answers = CompletableFuture.supplyAsync(() -> generateAnswers(parsedValues));
            answers.thenAccept(this::saveToDB);

        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    void saveToDB(List<SinIntegral> answers) {
        sinIntegralRepository.saveAll(answers);
    }

    @RequestMapping(value = "/bulk",
            method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity<?> getBulkAnswers(@RequestBody List<Double> values) {
        LOGGER.info("Post bulk start");
        LOGGER.info("parsing");
        if (values.size() % 2 != 0 || values.size() < 2) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<List<Double>> parsedValues;
        try {
            parsedValues = parseInput(values);
        }catch (OutOfboundExp exp)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<SinIntegral> ans = generateAnswers(parsedValues);
        sinIntegralRepository.saveAll(ans);
        JSONObject response = new JSONObject();
        LOGGER.info("Generate JSON");
        response.put("answers", ans);
        response.put("minInput", values.stream().min(Double::compare).orElse(null));
        response.put("avgInp", values.stream().mapToDouble(a -> a).average().orElse(Double.MIN_VALUE));
        response.put("maxInput", values.stream().max(Double::compare).orElse(null));
        response.put("minAns", ans.stream().mapToDouble(SinIntegral::getAns).min().orElse(Double.MIN_VALUE));
        response.put("maxAns", ans.stream().mapToDouble(SinIntegral::getAns).max().orElse(Double.MIN_VALUE));
        response.put("avgAns", ans.stream().mapToDouble(SinIntegral::getAns).average().orElse(Double.MIN_VALUE));
        response.put("amount", ans.size());
        LOGGER.info("GOOD ENDING!");
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<?> result(@PathVariable("id") Long id) {
        SinIntegral eq = sinIntegralRepository.findById(id).orElse(null);
        if (eq == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOGGER.info("get from database");
        return new ResponseEntity<>(eq, HttpStatus.OK);
    }

    public List<List<Double>> parseInput(List<Double> values) {
        LOGGER.info("Parsing");
        List<List<Double>> parsedValues = new ArrayList<>();
        for (int i = 0, j = 0; i < values.size(); i += 2, j++) {
            if(values.get(i)>values.get(i + 1))
            {
                throw new OutOfboundExp("bad values");
            }
            parsedValues.add(new ArrayList<>());
            parsedValues.get(j).add(values.get(i));
            parsedValues.get(j).add(values.get(i + 1));
        }
        return parsedValues;
    }

    public List<SinIntegral> generateAnswers(List<List<Double>> parsedValues) {

        LOGGER.info("Counting");
        List<SinIntegral> answers;
            answers = Stream.concat(
                            parsedValues.stream()
                                    .filter(value -> cache.contains(value.hashCode()))
                                    .map(value -> {
                                        LOGGER.info("Get from cache");
                                        return cache.get((long) value.hashCode());
                                    })
                            ,
                            parsedValues.stream()
                                    .filter(value -> !cache.contains(value.hashCode()))
                                    .map(value -> {
                                        Optional<SinIntegral> integralFromDB = sinIntegralRepository.findById((long) value.hashCode());
                                        if (integralFromDB.isEmpty()) {
                                            SinIntegral eq;
                                            LOGGER.info("count integral");
                                                eq = new SinIntegral(value.get(0), value.get(1));
                                            eq.setId((long) value.hashCode());
                                            cache.put(eq.getId(), eq);
                                            return eq;
                                        }
                                        LOGGER.info("Get from database");
                                        return integralFromDB.get();
                                    })
                    )
                    .toList();
        return answers;
    }
}
