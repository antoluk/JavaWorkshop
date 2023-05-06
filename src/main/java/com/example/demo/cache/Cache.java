package com.example.demo.cache;

import com.example.demo.logic.SinIntegral;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Cache {

    private final Map<Long, SinIntegral> cacheMap = new HashMap<>();

    public void put(Long key, SinIntegral value) {
        cacheMap.put(key, value);
    }

    public SinIntegral get(Long key) {
        return cacheMap.get(key);
    }

    public void remove(Long key) {
        cacheMap.remove(key);
    }

    public void clear() {
        cacheMap.clear();
    }

    public Boolean contains(int value) {
        return this.cacheMap.containsKey(value);
    }
}
