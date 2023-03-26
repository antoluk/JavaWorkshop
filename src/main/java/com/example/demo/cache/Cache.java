package com.example.demo.cache;

import java.util.*;

import com.example.demo.logic.SinIntegral;
import org.springframework.stereotype.Component;

@Component
public class Cache {

    private final Map<String, SinIntegral> cacheMap = new HashMap<>();

    public void put(String key, SinIntegral value) {
        cacheMap.put(key, value);
    }

    public SinIntegral get(String key) {
        return cacheMap.get(key);
    }

    public void remove(String key) {
        cacheMap.remove(key);
    }

    public void clear() {
        cacheMap.clear();
    }
}
